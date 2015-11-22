from pymongo import MongoClient
import server

def dummy_data():

    #Connect to DB
    client = MongoClient('mongodb://localhost:27017/')
    db = client['seg3102']
    users = db['users']
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
    course_3102 = courses.find_one({"courseCode" : "SEG 3102"})
    course_3101 = courses.find_one({"courseCode" : "SEG 3101"})
    team_params.replace_one(
                {
                    "courseId" : course_3102['_id'],
                    "minimumNumberOfStudents" : 2,
                    "maximumNumberOfStudents" : 4,
                    "deadline" : "November 2, 2015"
                },
                {
                    "courseId" : course_3102['_id'],
                    "minimumNumberOfStudents": "2",
                    "maximumNumberOfStudents": "4",
                    "deadline": "November 2, 2015"
                }
                , True)

    team_params.replace_one(
                {
                    "courseId" : course_3101['_id'],
                    "minimumNumberOfStudents": "2",
                    "maximumNumberOfStudents": "4",
                    "deadline": "November 2, 2015"
                },
                {
                    "courseId" : course_3101['_id'],
                    "minimumNumberOfStudents": 2,
                    "maximumNumberOfStudents": 4,
                    "deadline": "November 2, 2015"
                }
                , True)
    
