from collections import OrderedDict
from pymongo import MongoClient
from bson.objectid import ObjectId
from flask import Flask
from flask import jsonify
from flask import request
from datetime import datetime, timedelta
from flask_jwt import JWT, jwt_required, current_identity, JWTError
import bcrypt
import dummyData
import time

app = Flask(__name__)
app.config["DEBUG"] = True
app.config["SECRET_KEY"] = 'supercomplexrandomvalue'
app.config['JWT_EXPIRATION_DELTA'] = timedelta(seconds=7200) # token expires every 2 hours

client = MongoClient('mongodb://localhost:27017/')
db = client['seg3102']
users = db['users']
student_users = db['students']
instructor_users = db['instructors']
team_params = db['teamParams']
courses = db['courses']
teams = db['teams']


def authenticate(username, password):
    user = student_users.find_one({"username": username})
    user_type = "student"
    if user is None:
        user = instructor_users.find_one({"username": username})
        user_type = "instructor"
    elif teams.find_one({"liason": username}):
        user_type = "liason"
    if user:
        user['type'] = user_type
        passMatch = bcrypt.hashpw(password.encode('utf-8'), user['password'].encode('utf-8')) == user['password'].encode('utf-8')
        if passMatch:                    
            return user
        else:
            raise JWTError('Bad credentials', 'Incorrect password!', status_code=404)
    else:
        raise JWTError('Bad credentials', 'User not found!', status_code=404)

def identity(payload):
    user_id = payload['identity']
    user = None
    if user_id:
        user = student_users.find_one({"_id": ObjectId(user_id)})    
        if user is None:
            user = instructor_users.find_one({"_id": ObjectId(user_id)})                
    return user

def encrypt(password):
    return bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())

jwt = JWT(app, authenticate, identity)

@jwt.jwt_payload_handler
def payload_handler(identity):
    iat = datetime.utcnow()
    exp = iat + app.config.get('JWT_EXPIRATION_DELTA')
    nbf = iat + app.config.get('JWT_NOT_BEFORE_DELTA')    
    identity = str(identity["_id"])
    return {'exp': exp, 'iat': iat, 'nbf': nbf, 'identity': identity}

@jwt.auth_response_handler
def auth_response_handler(access_token, identity):
    return jsonify({
        'access_token': access_token.decode('utf-8'),
        'user_type': identity['type']
    })

@jwt.jwt_error_handler
def error_response_handler(error):
    return jsonify(OrderedDict([
        ('status_code', error.status_code),
        ('error', error.error),
        ('message', error.description),
    ])), error.status_code, error.headers


@app.route('/register', methods=['POST'])
def register():
    required_keys = ['username', 'password', 'email', 'first_name', 'last_name', 'user_type']
    validation = validate_data_format(request, required_keys)
    valid_format = validation[0]
    data = validation[1]
    if valid_format:
        username = request.json['username']
        password = request.json['password']
        email = request.json['email']
        f_name = request.json['first_name']
        l_name = request.json['last_name']
        user_type = request.json['user_type']

        #Check if user already exists
        if student_users.find_one({"username": username}):                    
            data['message'] = "Student with that username already exists"
        elif instructor_users.find_one({"username": username}):                    
            data['message'] = "Instructor with that username already exists"
        else:         
            if(user_type.strip().lower() == "student"):
                if 'programOfStudy' not in request.json:
                    data['message'] = "Program of Study was not specified"
                else:
                    program_of_study = request.json['programOfStudy']
                    res = student_users.insert_one({
                            "username": username,
                            "password": encrypt(password),
                            "email" : email,
                            "firstName" : f_name,
                            "lastName" : l_name,
                            "programOfStudy" : program_of_study
                        })
                    data['status'] = 200
                    data['message'] = 'Student successfully registered!'
            elif (user_type.strip().lower() == "instructor"):
                res = instructor_users.insert_one({
                            "username": username,
                            "password": encrypt(password),
                            "email" : email,
                            "firstName" : f_name,
                            "lastName" : l_name
                        })
                data['status'] = 200
                data['message'] = 'Instructor successfully registered!'
            else:
                data['message'] = 'The user type specified is not valid'

        
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp

@app.route('/protected', methods=['POST'])
@jwt_required()
def protected():
    return '%s' % current_identity

@app.route('/createTeamParams', methods=['POST'])
@jwt_required()
def create_team_params():
    user = instructor_users.find_one({'_id': current_identity['_id']})
    required_keys = ['course_code', 'course_section','minimum_num_students', 'maximum_num_students', 'deadline']
    validation = validate_data_format(request, required_keys)
    valid_format = validation[0]
    data = validation[1]

    if valid_format:
        if user:
                course_code = request.json['course_code']
                course_section = request.json['course_section']
                minimum_number_of_students = request.json['minimum_num_students']
                maximum_number_of_students = request.json['maximum_num_students'] 
                deadline = request.json['deadline']
                #SHOULD HAVE VALIDATION HERE THAT CHECKS WHETHER THE PARAMETERS ARE IN CORRECT FORMAT (DATE, INTEGER, ETC.)
                
                # Search for course by course code & section
                course = courses.find_one({"courseCode": course_code, "courseSection": course_section})
                if course is None:
                    data['message'] = "The course code with the specified section does not exist"
                else:
                    res = team_params.insert_one({
                            "instructorId" : user['_id'],
                            "courseId" : course['_id'],
                            "minimumNumberOfStudents": minimum_number_of_students,
                            "maximumNumberOfStudents": minimum_number_of_students,
                            "deadline": deadline
                        })
                    data['status'] = 200
                    data['message'] = 'Team Parameters were successfully created!'
        else:
            data['message'] = 'You do not have permission to create team parameters'
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp

@app.route('/teamParams', methods=['GET'])
@jwt_required()
def get_team_params():
    data = {}
    data['status'] = 200
    teamParams = []
    for row in team_params.find():
        course = courses.find_one({'_id': row['courseId']})
        instructor = instructor_users.find_one({'_id': row['InstructorId']})        
        obj = {
            "_id": str(row['_id']),
            "courseId": str(course['_id']),
            "InstructorId": str(instructor['_id']),
            "course_code": course['courseCode'],
            "course_section": course['courseSection'],
            "instructor_name": instructor['firstName'] + ' ' + instructor['lastName'],
            "deadline": row['deadline'],
            "minimumNumberOfStudents": row['minimumNumberOfStudents'],
            "maximumNumberOfStudents": row['maximumNumberOfStudents']           
        }
        teamParams.append(obj)        
    data['teamParams'] = teamParams
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp

@app.route('/createTeam', methods=['POST'])
@jwt_required()
def create_team():

    required_keys = ['team_param_id', 'team_name', 'team_members']
    validation = validate_data_format(request, required_keys)
    valid_format = validation[0]
    data = validation[1]

    if valid_format:
            team_param_id = request.json['team_param_id']
            team_name = request.json['team_name']
            team_members = request.json['team_members']
            liason = student_users.find_one({"_id" : current_identity['_id']})
            invalid_liason = True
            if liason:
                liason = liason['username']
                invalid_liason = False
                if liason not in team_members:
                    team_members.append(liason) #Add liason username if it is not already in 
            
            valid_info = invalid_object(team_param_id, team_params)
            invalid_team_param = valid_info[0]
            teamParam = valid_info[1]
            
            if invalid_liason:
                data['message'] = "You do not have permission to perform this operation"
            elif invalid_team_param:
                data['message'] = "No team parameter exists for the given team parameter ID"
            elif len(team_members) > teamParam['maximumNumberOfStudents']:
                data['message'] = "You have selected too many members, the maximum number of members allowed is "+ str(teamParam['maximumNumberOfStudents']) 
            elif len(team_members) < teamParam['minimumNumberOfStudents']:
                data['message'] = "You did not provide enough members, the minimum number of members allowed is "+ str(teamParam['minimumNumberOfStudents'])
            elif teams.find_one({'teamName' : team_name}): #Check within the teams with same teamparam (Valid to have different courses have teams with same name? 
                data['message'] = "A team already exists with the given team name"
            else:
                #Check if each username in the list of team_members received is a valid student user
                createTeam = True
                members = []
                for member in team_members:
                    if student_users.find_one({"username" : member}) is None:
                        createTeam = False
                        data['message'] = member + " is not a valid Student username"
                        break
                    members.append(member)

                if createTeam:
                    #Check if each student in team_members IS NOT in a team with the team param
                    list_of_teams = teams.find({"teamParameterId" : teamParam['_id']})
                    for team in list_of_teams:
                        for student in team_members:
                            if student in team['teamMembers']:
                                createTeam = False
                                data['message'] = student + ' is already in a team'
                                break

                #If createTeam is still true, then we can insert a new team into the database
                if createTeam:
                    #Check if members is less than max team size
                    less_than_max = len(members) < teamParam['maximumNumberOfStudents']
                    if less_than_max:
                        status = "incomplete"
                    else:
                        status = "complete"
                    
                    res = teams.insert_one({
                            "teamParamId" : teamParam['_id'],
                            "teamName" : team_name,
                            "dateOfCreation" : time.strftime("%c"),
                            "status" : status,
                            "teamSize" : len(members),
                            "teamMembers": members,
                            "liason" : liason,
                            "requestedMembers" : []
                            
                        })
                    data['status'] = 200
                    data['message'] = 'Team was successfully created!'
    
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp


@app.route('/students', methods=['GET'])
@jwt_required()
def get_students():
    data = {}
    data['status'] = 200
    list_of_students = []
    for row in student_users.find():
        obj = {
            "_id": str(row['_id']),
            "username": row['username'],
            "firstName": row['firstName'],
            "lastName": row['lastName'],
            "programOfStudy": row['programOfStudy'],
            "email": row['email']
            }
        list_of_students.append(obj)
    data['students'] = list_of_students
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp

#Use case : Visualize student Teams
@app.route('/teams', methods=['GET'])
@jwt_required()
def get_teams():
    data = {}
    data['status'] = 200
    list_of_teams = []
    for row in teams.find():
        row['_id'] = str(row['_id'])
        row['teamParamId'] = str(row['teamParamId'])
        list_of_teams.append(row)
    data['teams'] = list_of_teams
    resp = jsonify(data)
    resp.status_code = data['status']

    return resp

@app.route('/incompleteTeams', methods=['GET'])
@jwt_required()
def get_incomplete_teams():
    data = {}
    data['status'] = 200
    list_of_teams = []
    for row in teams.find({"status": "incomplete"}):
        row['_id'] = str(row['_id'])
        row['teamParamId'] = str(row['teamParamId'])
        list_of_teams.append(row)
        
        
    data['teams'] = list_of_teams
    resp = jsonify(data)
    resp.status_code = data['status']

    return resp

#Use case Join Team goes against our design. A student can only join if they are not in a team already
@app.route('/joinTeams', methods=['POST'])
@jwt_required()
def join_teams():
    data = {}
    required_keys = ['team_ids']
    validation = validate_data_format(request, required_keys)
    valid_format = validation[0]
    data = validation[1]

    if valid_format:
        team_ids = request.json['team_ids']
        #Check if team_ids are valid
        invalid_team_ids = False
        
        for id in team_ids:
            invalid_team_ids = invalid_object(id, teams)[0]
            if invalid_team_ids:
                break
        if invalid_team_ids:
            data['message'] = 'A team with id: ' + id + ' does not exist'
        else:
            user = student_users.find_one({"_id": current_identity['_id']})
            if user is None:
                user = instructor_users.find_one({"_id" : current_identity['_id']})
            username = user['username']
            for team in team_ids:
                current_team = teams.find_one({"_id" : ObjectId(team)})
                requests = current_team['requestedMembers']
                if username not in requests and username not in current_team['teamMembers']:
                    requests.append(username)
                teams.update_one(
                    {
                        "_id": current_team['_id']
                    },
                    {
                        "$set": {"requestedMembers": requests}
                    })
            data['status'] = 200
            data['message'] = 'Successfully joined teams'
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp

#View Requested members of a specified team. Use Case: Accept new Students
@app.route('/viewRequestedMembers', methods=['GET'])
@jwt_required()
def view_requested_members():
    data = {}
    data['status'] = 404
    current_user = student_users.find_one({"_id": ObjectId(current_identity['_id'])})
    
    if current_user:
        if 'team_id' in request.args:
            team_id = request.args['team_id']
            invalid_team_id= invalid_object(team_id, teams)[0]

            if invalid_team_id:
                data['message'] = "A team with id: " + team_id + " does not exist"
            elif current_user['username'] != team['liason']:
                data['message'] = "Only liasons of the requested team can perform this operation"
            else:
                list_of_requests = team['requestedMembers']
                data['requestedMembers'] = list_of_requests
                data['status'] = 200
                
        else: 
            data['message'] = "No team id was provided"
    else:
        data['message'] = "You do not have permission to perform this operation"

    resp = jsonify(data)
    resp.status_code = data['status']

    return resp

@app.route('/acceptMembers', methods=['POST'])
@jwt_required()
def accept_members():
    current_user = student_users.find_one({"_id": ObjectId(current_identity['_id'])})
    required_keys = ['team_id','list_of_usernames']
    validation = validate_data_format(request, required_keys)
    valid_format = validation[0]
    data = validation[1]

    if current_user is None:
        data['message'] = 'You do not have permission to accept new members'
    elif valid_format:  
        team_id = request.json['team_id']
        list_of_usernames = request.json['list_of_usernames']
        team_validation = invalid_object(team_id, teams) 
        invalid_team = team_validation[0]
        team = team_validation[1] # None if invalid _team is true
        invalid_users = False
        users_in_team = False
        
        if invalid_team:
            data['message'] = 'A team does not exist with the specified id'
        elif team['liason'] != current_user['username']:
            data['message'] = 'Only the liason of the team can perform this operation'
        elif len(list_of_usernames) == 0:
            data['message'] = "The members you would like to add to team must be provided"
        elif team['status'] == "complete":
            data['message'] = "The team selected already has the maximum number of members"
        else:
            for username in list_of_usernames:
                student = student_users.find_one({"username": username})
                if student is None:
                    invalid_users = True
                    break
                elif username in team['teamMembers']:
                    users_in_team = True
                    break

            team_param = team_params.find_one({"_id" : team['teamParamId']})
            max_students = team_param['maximumNumberOfStudents']

            if invalid_users:
                data['message'] = "No student exists with the username: " + username
            elif users_in_team: 
                data['message'] = username + " is already a member of the team"
            elif (len(list_of_usernames) + int(team['teamSize'])) > max_students:
                data['message'] = "Maximum number of students is exceeded if all selected students are added to team"
            else:
                members = team['teamMembers'] + list_of_usernames
                if len(members) == max_students:
                    status = "complete"
                else:
                    status = "incomplete"
                teams.update_one(
                    {
                        "_id": team['_id']
                    },
                    {
                        "$set": {"teamMembers": members, "status": status}
                    })
                data['message'] = "Successfully added selected users to team"
                data['status'] = 200
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp

#Return the teams with the specified team parameter 
@app.route('/teamsInTeamParam', methods=['GET'])
@jwt_required()
def get_teams_with_teamParam():
    data = {}
    data['status'] = 404
    current_user = student_users.find_one({"_id": ObjectId(current_identity['_id'])})
    if current_user is None:
        data['message'] = "You do not have permission to perform this operation"
    elif 'teamParam_id' in request.args:
        teamParam_id = request.args['teamParam_id']
        invalid_teamParam_id= invalid_object(teamParam_id, team_params)[0]

        if invalid_teamParam_id:
            data['message'] = "A team Parameter with id: '" + teamParam_id + "' does not exist"
        else:
            list_of_teams = []
            for team in teams.find({'teamParamId' : ObjectId(teamParam_id)}):
                team['teamParamId'] = str(team['teamParamId'])
                team['_id'] = str(team['_id'])
                list_of_teams.append(team)

            data['list_of_teams'] = list_of_teams
            data['status'] = 200
            
    else: 
        data['message'] = "The team Parameter was not provided"


    resp = jsonify(data)
    resp.status_code = data['status']

    return resp


#Return the teams the current user is a liason for
@app.route('/liasionTeams', methods=['GET'])
@jwt_required()
def get_liasion_teams():
    data = {}
    data['status'] = 404
    current_user = student_users.find_one({"_id" : current_identity['_id']})
    if current_user:
        db_teams = teams.find({"liason" : current_user['username']})
        number_of_teams = db_teams.count()
        if number_of_teams == 0:
            data['message'] = "You are not a liasion of any team"
        else:
            list_of_teams = []
            for team in db_teams:
                team['teamParamId'] = str(team['teamParamId'])
                team['_id'] = str(team['_id'])
                list_of_teams.append(team)
        
            data['teams'] = list_of_teams
    else:
        data['message'] = "You do not have permission to perform this operation"        


    resp = jsonify(data)
    resp.status_code = data['status']

    return resp



#Validates object based on team_id and the specified db to search in
def invalid_object(id, database):
    invalid_id = False
    try:
        obj = database.find_one({"_id" : ObjectId(id)})
        if obj is None:
            invalid_id = True
    except: 
        invalid_id = True
        obj= None
    return (invalid_id, obj)

def validate_data_format (request, required_keys):
    data = {}
    data['status'] = 404   
    valid_data = True 
    if not request.json:
        data['message'] = 'No data was provided'
        valid_data = False
    else:
        valid_data = all(key in request.json for key in required_keys) # Check if request.json contains all the required keys  
        if valid_data:
            valid_data = "" not in request.json.viewvalues() # Check if request.json contains content for all values  
        if valid_data == False:
            data['message'] = 'All fields must be provided!' 
    return (valid_data, data)


if __name__ == "__main__":
    dummyData.dummy_data()
    app.run(port=3001)    

