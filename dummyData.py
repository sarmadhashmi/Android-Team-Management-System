from pymongo import MongoClient
import server
from datetime import datetime
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
                            "email" : "muraad@uottawa.ca",
                            "firstName" : "Muraad",
                            "lastName" : "Hared",
                            "programOfStudy" : "SEG"
                            }
                            , True)

    student_users.replace_one({"username" : "stest2"},
                            {
                            "username": "stest2",
                            "password": server.encrypt("test"),
                            "email" : "sarmad@uottawa.ca",
                            "firstName" : "Sarmad",
                            "lastName" : "Hashmi",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    student_users.replace_one({"username" : "stest3"},
                            {
                            "username": "stest3",
                            "password": server.encrypt("test"),
                            "email" : "salman@uottawa.ca",
                            "firstName" : "Salman",
                            "lastName" : "Rana",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    student_users.replace_one({"username" : "stest4"},
                            {
                            "username": "stest4",
                            "password": server.encrypt("test"),
                            "email" : "janac@uottawa.ca",
                            "firstName" : "Janac",
                            "lastName" : "Meena",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    student_users.replace_one({"username" : "stest5"},
                            {
                            "username": "stest5",
                            "password": server.encrypt("test"),
                            "email" : "james@uottawa.ca",
                            "firstName" : "James",
                            "lastName" : "Bond",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    student_users.replace_one({"username" : "stest6"},
                            {
                            "username": "stest6",
                            "password": server.encrypt("test"),
                            "email" : "bernard@uottawa.ca",
                            "firstName" : "Bernard",
                            "lastName" : "Jackson",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    student_users.replace_one({"username" : "stest7"},
                            {
                            "username": "stest7",
                            "password": server.encrypt("test"),
                            "email" : "barry@uottawa.ca",
                            "firstName" : "Barry",
                            "lastName" : "Allen",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    student_users.replace_one({"username" : "stest8"},
                            {
                            "username": "stest8",
                            "password": server.encrypt("test"),
                            "email" : "bobby@uottawa.ca",
                            "firstName" : "Bobby",
                            "lastName" : "Builder",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    student_users.replace_one({"username" : "stest9"},
                            {
                            "username": "stest9",
                            "password": server.encrypt("test"),
                            "email" : "peter@uottawa.ca",
                            "firstName" : "Peter",
                            "lastName" : "Parker",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    student_users.replace_one({"username" : "stest10"},
                            {
                            "username": "stest10",
                            "password": server.encrypt("test"),
                            "email" : "joe@uottawa.ca",
                            "firstName" : "Joe",
                            "lastName" : "Johnson",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    student_users.replace_one({"username" : "stest11"},
                            {
                            "username": "stest11",
                            "password": server.encrypt("test"),
                            "email" : "tim@uottawa.ca",
                            "firstName" : "Tim",
                            "lastName" : "Turner",
                            "programOfStudy" : "SEG"
                            }
                            , True)
    
    student_users.replace_one({"username" : "reqTest"},
                            {
                            "username": "reqTest",
                            "password": server.encrypt("test"),
                            "email" : "snake2@uottawa.ca",
                            "firstName" : "Johnny",
                            "lastName" : "Razor",
                            "programOfStudy" : "SEG"
                            }
                            , True)

    student_users.replace_one({"username" : "reqTest2"},
                            {
                            "username": "reqTest2",
                            "password": server.encrypt("test"),
                            "email" : "snake2@uottawa.ca",
                            "firstName" : "Samantha",
                            "lastName" : "Melo",
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
                            "lastName" : "Muraad",
                            "programOfStudy" : "SEG"
                            }
                            , True)

    instructor_users.replace_one({"username" : "test2"},
                            {
                            "username": "test2",
                            "password": server.encrypt("test"),
                            "email" : "instructor2@uottawa.ca",
                            "firstName" : "Instructor",
                            "lastName" : "Hashmi",
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
                    "instructorId" : instructor['_id'],
                    "courseId" : course_3102['_id'],
                    "minimumNumberOfStudents" : 2,
                    "maximumNumberOfStudents" : 4,
                    "deadline" : "02/11/2017 12:54:00"
                },
                {
                    "instructorId" : instructor['_id'],
                    "courseId" : course_3102['_id'],
                    "minimumNumberOfStudents": 2,
                    "maximumNumberOfStudents": 4,
                    "deadline": "02/11/2017 12:54:00"
                }
                , True)

    team_params.replace_one(
                {
                    "instructorId" : instructor['_id'],
                    "courseId" : course_3101['_id'],
                    "minimumNumberOfStudents": 2,
                    "maximumNumberOfStudents": 4,
                    "deadline": "20/05/2017 23:59:00"
                },
                {
                    "instructorId" : instructor['_id'],
                    "courseId" : course_3101['_id'],
                    "minimumNumberOfStudents": 2,
                    "maximumNumberOfStudents": 4,
                    "deadline": "20/05/2017 23:59:00"
                }
                , True)

    #Teams
    team_params_1 = team_params.find_one({"courseId": course_3101['_id']})
    team_params_2 = team_params.find_one({"courseId": course_3102['_id']})
    teams.replace_one(
                {
                    "teamName" : "SEG Four"
                },
                {
                    "teamParamId" : team_params_1['_id'],
                    "teamName" : "SEG Four",
                    "dateOfCreation" : datetime.now().strftime('%d/%m/%Y %H:%M:%S'),
                    "status" : "complete",
                    "teamSize" : 4,
                    "teamMembers": ["stest2", "stest3", "stest4", "stest5"],
                    "liason" : "stest2",
                    "requestedMembers" : []                        
                }, True)
    
    teams.replace_one(
                {
                    "teamName" : "Binary Team"
                },
                {
                    "teamParamId" : team_params_1['_id'],
                    "teamName" : "Binary Team",
                    "dateOfCreation" : datetime.now().strftime('%d/%m/%Y %H:%M:%S'),
                    "status" : "incomplete",
                    "teamSize" : 2,
                    "teamMembers": ["stest", "stest6"],
                    "liason" : "stest",
                    "requestedMembers" : ["stest7","stest8","stest9"]                        
                }, True)

    teams.replace_one(
                {
                    "teamName" : "COOP Team"
                },
                {
                    "teamParamId" : team_params_1['_id'],
                    "teamName" : "COOP Team",
                    "dateOfCreation" : datetime.now().strftime('%d/%m/%Y %H:%M:%S'),
                    "status" : "incomplete",
                    "teamSize" : 2,
                    "teamMembers": ["stest10", "stest11"],
                    "liason" : "stest10",
                    "requestedMembers" : []                        
                }, True)
    
    
    teams.replace_one(
                {
                    "teamName" : "Best Team"
                },
                {
                    "teamParamId" : team_params_2['_id'],
                    "teamName" : "Best Team",
                    "dateOfCreation" : datetime.now().strftime('%d/%m/%Y %H:%M:%S'),
                    "status" : "complete",
                    "teamSize" : 4,
                    "teamMembers": ["stest2", "stest10", "stest11", "stest9"],
                    "liason" : "stest2",
                    "requestedMembers" : []                        
                }, True)
    
    teams.replace_one(
                {
                    "teamName" : "Two Squad"
                },
                {
                    "teamParamId" : team_params_2['_id'],
                    "teamName" : "Two Squad",
                    "dateOfCreation" : datetime.now().strftime('%d/%m/%Y %H:%M:%S'),
                    "status" : "incomplete",
                    "teamSize" : 2,
                    "teamMembers": ["stest", "stest8"],
                    "liason" : "stest",
                    "requestedMembers" : ["stest3","stest4","stest5"]                        
                }, True)

    teams.replace_one(
                {
                    "teamName" : "Incomplete Fellows"
                },
                {
                    "teamParamId" : team_params_2['_id'],
                    "teamName" : "Incomplete Fellows",
                    "dateOfCreation" : datetime.now().strftime('%d/%m/%Y %H:%M:%S'),
                    "status" : "incomplete",
                    "teamSize" : 2,
                    "teamMembers": ["stest6", "stest7"],
                    "liason" : "stest6",
                    "requestedMembers" : []                        
                }, True)
    
