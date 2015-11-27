from pymongo import MongoClient
import server, time

def dummy_data():

    #Connect to DB
    client = MongoClient('mongodb://localhost:27017/')
    db = client['seg3102']
    users = db['users']
    teams = db['teams']
    student_users = db['students']
    instructor_users = db['instructors']
    team_params = db['teamParams']
    courses = db['courses']

    #inserting Dummy Data IF data doesn't already exist

    #Insert Users
    ##Insert Students
    student_users.replace_one({"username" : "stest"},
                            {
                            "username": "stest",
                            "password": server.encrypt("test"),
                            "email" : "snake@uottawa.ca",
                            "firstName" : "Student",
                            "lastName" : "Tester",
                            "programOfStudy" : "SEG"
                            }
                            , True)

    student_users.replace_one({"username" : "stest2"},
                            {
                            "username": "stest2",
                            "password": server.encrypt("test"),
                            "email" : "snake2@uottawa.ca",
                            "firstName" : "Student2",
                            "lastName" : "Tester2",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    student_users.replace_one({"username" : "reqTest"},
                            {
                            "username": "reqTest",
                            "password": server.encrypt("test"),
                            "email" : "snake2@uottawa.ca",
                            "firstName" : "reqTest",
                            "lastName" : "Tester2",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    ##Insert Instructors 
    instructor_users.replace_one({"username" : "test"},
                            {
                            "username": "test",
                            "password": server.encrypt("test"),
                            "email" : "instructor@uottawa.ca",
                            "firstName" : "Instructor",
                            "lastName" : "Tester",
                            "programOfStudy" : "SEG"
                            }
                            , True)

    instructor_users.replace_one({"username" : "test2"},
                            {
                            "username": "test2",
                            "password": server.encrypt("test"),
                            "email" : "instructor2@uottawa.ca",
                            "firstName" : "Instructor2",
                            "lastName" : "Tester2",
                            "programOfStudy" : "SEG"
                            }
                             , True)

    ##Courses
    courses.replace_one({"courseCode": "SEG 3102"},
                   {
                        "courseCode": "SEG 3102",
                        "courseSection": "A"
                    }, True)
    courses.replace_one({"courseCode": "SEG 3101"},
                   {
                        "courseCode": "SEG 3101",
                        "courseSection": "B"
                    }, True)

    ##Team Paramters
    instructor = instructor_users.find_one({"username" : "test"})
    course_3102 = courses.find_one({"courseCode" : "SEG 3102", "courseSection" : "A"})
    course_3101 = courses.find_one({"courseCode" : "SEG 3101", "courseSection" : "B"})
    team_params.replace_one(
                {
                    "InstructorId" : instructor['_id'],
                    "courseId" : course_3102['_id'],
                    "minimumNumberOfStudents" : 2,
                    "maximumNumberOfStudents" : 4,
                    "deadline" : "02/11/2017 12:54"
                },
                {
                    "InstructorId" : instructor['_id'],
                    "courseId" : course_3102['_id'],
                    "minimumNumberOfStudents": 2,
                    "maximumNumberOfStudents": 4,
                    "deadline": "02/11/2017 12:54"
                }
                , True)

    team_params.replace_one(
                {
                    "InstructorId" : instructor['_id'],
                    "courseId" : course_3101['_id'],
                    "minimumNumberOfStudents": 2,
                    "maximumNumberOfStudents": 4,
                    "deadline": "20/05/2017 23:59"
                },
                {
                    "InstructorId" : instructor['_id'],
                    "courseId" : course_3101['_id'],
                    "minimumNumberOfStudents": 2,
                    "maximumNumberOfStudents": 4,
                    "deadline": "20/05/2017 23:59"
                }
                , True)

    #Teams
    team_params_1 = team_params.find_one({"courseId": course_3101['_id']})
    team_params_2 = team_params.find_one({"courseId": course_3101['_id']})
    teams.replace_one(
                {
                    "teamName" : "SnakeFour"
                },
                {
                    "teamParamId" : team_params_1['_id'],
                    "teamName" : "SnakeFour",
                    "dateOfCreation" : time.strftime("%c"),
                    "status" : "complete",
                    "teamSize" : 4,
                    "teamMembers": ["Salman", "Janac", "test", "Sarmad"],
                    "liason" : "stest",
                    "requestedMembers" : ["Muraad"]                        
                }, True)
    
    teams.replace_one(
                {
                    "teamName" : "NoRequestedMembers"
                },
                {
                    "teamParamId" : team_params_1['_id'],
                    "teamName" : "NoRequestedMembers",
                    "dateOfCreation" : time.strftime("%c"),
                    "status" : "incomplete",
                    "teamSize" : 3,
                    "teamMembers": ["stest", "stest2", "Janac"],
                    "liason" : "stest",
                    "requestedMembers" : []                        
                }, True)
    
    teams.replace_one(
                {
                    "teamName" : "OneMember"
                },
                {
                    "teamParamId" : team_params_2['_id'],
                    "teamName" : "OneMember",
                    "dateOfCreation" : time.strftime("%c"),
                    "status" : "incomplete",
                    "teamSize" : 1,
                    "teamMembers": ["stest"],
                    "liason" : "stest",
                    "requestedMembers" : ["reqTest"]                        
                }, True)

    teams.replace_one(
                {
                    "teamName" : "OneMember"
                },
                {
                    "teamParamId" : team_params_2['_id'],
                    "teamName" : "OneMember",
                    "dateOfCreation" : time.strftime("%c"),
                    "status" : "incomplete",
                    "teamSize" : 1,
                    "teamMembers": ["stest"],
                    "liason" : "stest",
                    "requestedMembers" : ["reqTest"]                        
                }, True)
    
