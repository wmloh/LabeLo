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
    id = db.Column(db.String(128), primary_key=True)
    name = db.Column(db.String(80), unique=True, nullable=False)
    label = db.Column(db.String(40), unique=True, nullable=False)

    def __repr__(self):
        return self.label

@app.route('/')
def test():
    return 'upload, ' + 'sendstr<string>, ' + ', sendimg' + ', getlabels'

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
    return 'Received ' + string

@app.route('/getimg')
def get_img():
    return send_file('./img/cat.1.jpg', mimetype='image/jpg')

@app.route('/sendlabel/<param>')
def send_label(param):
    img, lab = param.split('=')
    img = img.encode('utf-8')
    lab = lab.encode('utf-8')
    hash_str = sha256(img).hexdigest()
    db.session.add(Label(id=hash_str, name=img, label=lab))
    db.session.commit()
    return 'Received %s; %s; %s' % (hash_str, img, lab)

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
