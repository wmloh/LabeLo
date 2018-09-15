from flask import Flask, request, render_template, send_file, jsonify
from flask_sqlalchemy import SQLAlchemy
import os
import glob
import json
from hashlib import sha256

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////tmp/test.db'
db = SQLAlchemy(app)


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)

    def __repr__(self):
        return '<User %r>' % self.username

class Label(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    label = db.Column(db.String(40), unique=True, nullable=False)

    def __repr__(self):
        return self.label

@app.route('/')
def test():
    links = ('upload', 'sendstr<string>', 'sendimg', 'getlabels')
    return links

@app.route('/upload')
def upload_html():
    return render_template('index.html')

@app.route('/uploadfile', methods=['GET', 'POST'])
def upload_file():
    file = request.files['file']
    filename = file.save(file.filename)
    return 'Filed saved'

@app.route('/sendstr/<string>')
def send_str(string):
    hash_str = sha256(string.encode('utf8')).hexdigest()
    db.session.add(Label(id=hash_str, label=string))
    db.session.commit()
    return 'Received ' + string

@app.route('/getimg')
def send_img():
    return send_file('./img/cat.1.jpg', mimetype='image/jpg')

@app.route('/sendlabel')
def send_label():
    pass

@app.route('/getlabels', methods=['GET'])
def get_labels():
    json_file_dir = os.getcwd() + '/' + glob.glob('img/*.json')[0]
    with open(json_file_dir) as f:
        data = json.load(f)
    return jsonify(data)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)

db.create_all()

#35.185.87.150
