from pymongo import MongoClient
from flask import Flask
from flask import jsonify
from flask import request

app = Flask(__name__)
client = MongoClient('mongodb://localhost:27017/')
db = client['seg3102']
users = db['users']

@app.route('/register', methods=['POST'])
def register():    
    if not request.form:
        status = 404
        message = 'No data was provided'
    else:
        username = request.form['username']
        password = request.form['password']        
        if username is None or password is None:        
            message = 'Username and/or password not provided!'
            status = 404        
        elif users.find_one({"username": username}):        
            status = 404
            message = "User with that username already exists"
        else:
            status = 200
            message = 'User registered!'
            res = users.insert_one({
                "username": username,
                "password": password
            })
    resp = jsonify({
        'status': status,
        'message': message
    })
    resp.status_code = status    
    return resp
        
def login():
    

if __name__ == "__main__":
    app.run(port=3001, host='0.0.0.0')
