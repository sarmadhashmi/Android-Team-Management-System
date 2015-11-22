from pymongo import MongoClient
from bson.objectid import ObjectId
from flask import Flask
from flask import jsonify
from flask import request
from datetime import datetime
from flask_jwt import JWT, jwt_required, current_identity, JWTError
import bcrypt
import dummyData
import time

app = Flask(__name__)
app.config["DEBUG"] = True
app.config["SECRET_KEY"] = 'supercomplexrandomvalue'

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
    if user_id:     
        return users.find_one({"_id": ObjectId(user_id)})
    return None

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

@app.route('/register', methods=['POST'])
def register():
    data = {}
    data['status'] = 404
    required_keys = ['username', 'password', 'email', 'first_name', 'last_name', 'user_type']
    if not request.json:
        data['message'] = 'No data was provided'
    elif all(key in request.json for key in required_keys):        # Check if request.json contains all the required keys  

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
    else :
        data['message'] = 'All required fields were not provided!'
               
        
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp

@app.route('/protected', methods=['POST'])
@jwt_required()
def protected():
    return '%s' % current_identity

@app.route('/createTeamParams', methods=['POST'])
#@jwt_required()
def create_team_params():
    data = {}
    #user_id = current_identity['_id']
    #if instructor.findOne({'_id':current_identity['_id']})
    required_keys = ['course_code', 'course_section','minimum_num_students', 'maximum_num_students', 'deadline']
    data['status'] = 404
    if not request.json:
        data['message'] = 'No data was provided'
    elif all(key in request.json for key in required_keys):        # Check if request.json contains all the required keys        
            course_code = request.json['course_code']
            course_section = request.json['course_section']
            minimum_number_of_students = request.json['minimum_num_students']
            maximum_number_of_students = request.json['maximum_num_students'] 
            deadline = request.json['deadline']
            #SHOULD HAVE VALIDATION HERE THAT CHECKS WHETHER THE PARAMETERS ARE IN CORRECT FORMAT (DATE, INTEGER, ETC.)
            
            # Search for course by course code & section
            course = courses.find_one({"courseCode": course_code, "courseSection": course_section})
            if course is None:
                data['message'] = "The course code given does not exist"
            else:
                res = team_params.insert_one({
                        "courseId" : course['_id'],
                        "minimumNumberOfStudents": minimum_number_of_students,
                        "maximumNumberOfStudents": minimum_number_of_students,
                        "deadline": deadline
                    })
                data['status'] = 200
                data['message'] = 'Team Parameters were successfully created!'
    else:
        data['message'] = 'All fields must be provided!'                
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp

@app.route('/teamParams', methods=['GET'])
def get_team_params():
    data = {}
    data['status'] = 200
    teamParams = []
    for row in team_params.find():
        row['_id'] = str(row['_id'])
        course = courses.find_one({'_id': row['courseId']})
        row['courseId'] = str(row['courseId'])
        row['course_code'] = course['courseCode']
        row['course_section'] = course['courseSection']
        teamParams.append(row)
        
        
    data['teamParams'] = teamParams
    resp = jsonify(data)
    resp.status_code = data['status']

    return resp

@app.route('/createTeam', methods=['POST'])
def create_team():
    data = {}
    required_keys = ['team_param_id', 'team_name', 'team_members']
    #user_id = current_identity['_id']
    #if instructor.findOne({'_id':current_identity['_id']})
    data['status'] = 404    
    if not request.json:
        data['message'] = 'No data was provided'
    elif all(key in request.json for key in required_keys):        # Check if request.json contains all the required keys        
            team_param_id = request.json['team_param_id']
            team_name = request.json['team_name']
            team_members = request.json['team_members']
            error = False
            try:
                teamParam = team_params.find_one({'_id' : ObjectId(team_param_id)})
            except: 
                error = True #invalid teamParam id
            if error:
                data['message'] = "No team parameter exists for the given id"
            elif teams.find_one({'teamName' : team_name}):
                data['message'] = "A team already exists with the given team name"
            else:
                #Check if each student in team_members IS NOT in a team
                #TODO
                createTeam = True
                members = []
                for member in team_members:
                    if student_users.find_one({"username" : member}) is None:
                        createTeam = False
                        data['message'] = member + " is not a valid Student username"
                        break
                    members.append(member)

                if createTeam:
                    #Check if members is less than max team size
                    teamParam = team_params.find_one({'_id' : ObjectId(team_param_id)})
                    less_than_max = len(members) <teamParam['maximumNumberOfStudents']
                    if less_than_max:
                        status = "incomplete"
                    else:
                        status = "complete"
                    
                    res = teams.insert_one({
                            "teamParamId" : team_param_id,
                            "teamName" : team_name,
                            "dateOfCreation" : time.strftime("%c"),
                            "status" : status,
                            "teamSize" : len(members),
                            "teamMembers": members,
                            "liason" : "ID OF CURRENT USER",
                            "requestedMembers" : []
                            
                        })
                    data['status'] = 200
                    data['message'] = 'Team was successfully created!'
    else:
        data['message'] = 'All fields must be provided!'                
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp

#Use case : Visualize student Teams
@app.route('/teams', methods=['GET'])
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
# TODO figure this out and make the appropriate change
@app.route('/joinTeams', methods=['POST'])
def join_teams():
    data = {}
    required_keys = ['username', 'team_ids']
    data['status'] = 404    
    if not request.json:
        data['message'] = 'No data was provided'
    elif all(key in request.json for key in required_keys):        # Check if request.json contains all the required keys   
        team_ids = request.json['team_ids']
        username = request.json['username']
        ##Check for invalid Team ids? no way this can happend tho..
        for team in team_ids:
            current_team = teams.find_one({"_id" : ObjectId(team)})
            requests = current_team['requestedMembers']
            if username not in requests:
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
    else: 
        data['message'] = 'Username or List of teams was not provided!'  
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp


if __name__ == "__main__":
    dummyData.dummy_data()
    app.run(port=3001, host='0.0.0.0')    

