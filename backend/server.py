from flask import Flask, request, render_template, send_file, jsonify
from flask_sqlalchemy import SQLAlchemy
import os
import glob
import json
from hashlib import sha256
import numpy as np

import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from email.mime.base import MIMEBase
from email import encoders

url = 'http://35.185.87.150:5000'

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////tmp/test.db'
app.config['SQLALCHEMY_BINDS'] = {
    'db1': app.config['SQLALCHEMY_DATABASE_URI'],
    'db2': 'sqlite:////tmp/test2.db',
    'db3': 'sqlite:////tmp/test3.db'
    }
db = SQLAlchemy(app)

### Default Routes
@app.route('/')
def test():
    return 'General: upload\n' +\
           'Label: getimg/(name), sendlabel/(param), getlabels\n'  + \
           'Player: add/(name), update/(param), refresh/(player)'

@app.route('/dev/upload')
def upload_html():
    return render_template('index.html')

@app.route('/uploadfile', methods=['GET', 'POST'])
def upload_file():
    file = request.files['file']
    filename = file.save(file.filename)
    return 'Filed saved'

@app.route('/dev/reset')
def reset():
    db.drop_all()
    db.create_all()
    return 'Reset completed!'

@app.route('/dev/printdb')
def printdb():
    label = ''
    query_label = db.session.query(Label).all()
    for i in query_label:
        label += '%s: %s %s<br />' % (i.name, i.label, i.player)

    player = ''
    query_player = db.session.query(Player).all()
    for i in query_player:
        player += '%s: %i %i %i<br />' % (i.name, i.correct, i.wrong, i.rank)

    truth = ''
    query_truth = db.session.query(Truth).all()
    for i in query_truth:
        truth += '%s: %s<br />' % (i.name, i.truth)
        
    return '<p> LABEL </p><p> %s </p><p> PLAYER </p><p> %s </p><p> TRUTH </p><p> %s </p>' % (label, player, truth)

### Label Routes
class Label(db.Model):
    __bind_key__ = 'db1'
    row = db.Column(db.Integer, primary_key=True)
    id = db.Column(db.String(128), unique=False)
    name = db.Column(db.String(80), unique=False, nullable=False)
    label = db.Column(db.String(40), unique=False, nullable=False)
    player = db.Column(db.String(40), unique=False, nullable=False)
    
    def __repr__(self):
        return self.name

@app.route('/getimg/<img>')
def get_img(img):
    return send_file('img/' + img, mimetype='image/jpg')

@app.route('/sendlabel/<param>', methods=['POST'])
def send_label(param):
    img, lab, plyr = param.split('=')
    img = img.encode('utf-8')
    lab = lab.encode('utf-8')
    plyr = plyr.encode('utf-8')
    hash_str = sha256(img).hexdigest()
    db.session.add(Label(row=db.session.query(Label).count(),
                         id=hash_str,
                         name=img,
                         label=lab,
                         player=plyr))
    db.session.commit()
    
    return 'Received %s; %s; %sl %s' % (hash_str, img, lab, plyr)

@app.route('/getlabels', methods=['GET'])
def get_labels():
    json_file_dir = os.getcwd() + '/' + glob.glob('img/*.json')[0]
    with open(json_file_dir) as f:
        data = json.load(f)
    return jsonify(data)

### Player Routes
class Player(db.Model):
    __bind_key__ = 'db2'
    id = db.Column(db.String(128), primary_key=True)
    name = db.Column(db.String(80), unique=True, nullable=False)
    correct = db.Column(db.Integer)
    wrong = db.Column(db.Integer)
    rank = db.Column(db.Integer)

    def __repr__(self):
        return self.name

@app.route('/add/<name>')
def add_player(name):
    hash_str = sha256(name).hexdigest()
    db.session.add(Player(id=hash_str, name=name, correct=0, wrong=0, rank=0))
    db.session.commit()
    return 'Received %s; %s; 0; 0; 0' % (hash_str, name)

@app.route('/update/<param>')
def update_player(param):
    name, val = param.split('=')
    player = Player.query.filter_by(name=name).first()
    if val == '+1':
        player.correct += 1
    elif val == '-1':
        player.wrong += 1
    else:
        raise ValueError('Check the parameters of the route.')
    db.session.commit()
    
    return 'Current correct: %i, wrong: %i' % (player.correct, player.wrong)

@app.route('/refresh/<name>')
def refresh_player(name):
    player = Player.query.filter_by(name=name).first()
    player.correct = 0
    player.wrong = 0

    db.session.commit()
    return 'Refreshed successfully'

### Gameplay Functions

class Truth(db.Model):
    __bind_key__ = 'db3'
    num = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(40), unique=False, nullable=False)
    truth = db.Column(db.String(40), unique=False, nullable=False)
    def __repr__(self):
        return self.name

@app.route('/evaluate/<img_name>')
def evaluate(img_name):
    img_rows = Label.query.filter_by(name=img_name).all()
    json_file_dir = os.getcwd() + '/' + glob.glob('img/*.json')[0]
    with open(json_file_dir) as f:
        data = json.load(f)
    labels = data['labels']
    counts = list()
    for i in labels:
        i = i.encode('utf-8')
        count = 0
        for j in img_rows:
            if j.label == i:
                count += 1
        counts.append(count)

    counts = np.array(counts, dtype=np.float32)
    highest = np.max(counts)
    pos_highest = np.argmax(counts)
    count_sum = sum(counts)
    with open('truth.json', 'r') as f:
        data = json.load(f)
        
    if img_name in data:
        ground_truth = data[img_name]
        confidence = 1.
    else:
        ground_truth = labels[pos_highest]
        confidence = highest / count_sum

    for i in img_rows:
        if i.label == ground_truth:
            Player.query.filter_by(name=i.player).first().rank += 10
        else:
            Player.query.filter_by(name=i.player).first().rank -= 10
    db.session.add(Truth(num=db.session.query(Truth).count(),
                         name=img_name,
                         truth=ground_truth))
    db.session.commit()
    return '%s-%f' % (ground_truth, confidence)

@app.route('/sendresults/<email>')
def send_results(email):
    print(email)
    label_dict = dict()
    query_name = db.session.query(Label.name).all()
    query_label = db.session.query(Label.label).all()

    for i,j in zip(query_name, query_label):
        print(i,j)
        label_dict[i.name.encode('utf-8')] = j.label.encode('utf-8')
    print(label_dict)
    label_json = json.dumps(label_dict)

    with open('results.json', 'w') as f:
        f.write(label_json)
    
    server = smtplib.SMTP('smtp.gmail.com:587')
    server.ehlo()
    server.starttls()
    server.login('htn2018.test@gmail.com', 'hackthenorth18')

    msg = MIMEMultipart()
    msg['From'] = 'htn2018.test@gmail.com'
    msg['To'] = email
    msg['Subject'] = 'Results'
    body = 'Results of labelling exercise.\n'

    msg.attach(MIMEText(body, 'plain'))
    

    attachment = open('results.json', 'rb')
    part = MIMEBase('application', 'octet-stream')
    part.set_payload(attachment.read())
    encoders.encode_base64(part)
    part.add_header('Content-Disposition', "attachment; filename=results.json")

    msg.attach(part)
    text = msg.as_string()
    
    server.sendmail('htn2018.test@gmail.com', email, text)
    server.quit()
    
    return 'sent email'
    

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)

