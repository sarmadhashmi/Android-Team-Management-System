from pymongo import MongoClient
from bson.objectid import ObjectId
from flask import Flask
from flask import jsonify
from flask import request
from datetime import datetime
from flask_jwt import JWT, jwt_required, current_identity, JWTError
import bcrypt

app = Flask(__name__)
app.config["DEBUG"] = True
app.config["SECRET_KEY"] = 'supercomplexrandomvalue'

client = MongoClient('mongodb://localhost:27017/')
db = client['seg3102']
users = db['users']

def authenticate(username, password):
    user = users.find_one({"username": username})        
    if user:
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

@app.route('/register', methods=['POST'])
def register():
    data = {}
    data['status'] = 404
    if not request.form:
        data['message'] = 'No data was provided'
    else:
        username = request.form['username']
        password = request.form['password']        
        if username is None or password is None:        
            data['message'] = 'Username and/or password not provided!'        
        elif users.find_one({"username": username}):                    
            data['message'] = "User with that username already exists"
        else:
            data['status'] = 200
            data['message'] = 'User registered!'
            res = users.insert_one({
                "username": username,
                "password": encrypt(password)
            })
    resp = jsonify(data)
    resp.status_code = data['status']
    return resp

@app.route('/protected', methods=['POST'])
@jwt_required()
def protected():
    return '%s' % current_identity


if __name__ == "__main__":
    app.run(port=3001, host='0.0.0.0')
