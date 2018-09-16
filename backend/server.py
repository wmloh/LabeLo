from flask import Flask, request, render_template, send_file, jsonify
from flask_sqlalchemy import SQLAlchemy
import os
import glob
import json
from hashlib import sha256
import numpy as np

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

@app.route('/upload')
def upload_html():
    return render_template('index.html')

@app.route('/uploadfile', methods=['GET', 'POST'])
def upload_file():
    file = request.files['file']
    filename = file.save(file.filename)
    return 'Filed saved'

cur_row = 0
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

@app.route('/sendlabel/<param>')
def send_label(param):
    global cur_row
    img, lab, plyr = param.split('=')
    img = img.encode('utf-8')
    lab = lab.encode('utf-8')
    plyr = plyr.encode('utf-8')
    hash_str = sha256(img).hexdigest()
    db.session.add(Label(row=cur_row, id=hash_str, name=img, label=lab, player=plyr))
    db.session.commit()
    cur_row += 1
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

truth_num = 0
class Truth(db.Model):
    __bind_key__ = 'db3'
    num = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(40), unique=False, nullable=False)
    truth = db.Column(db.String(40), unique=False, nullable=False)
    def __repr__(self):
        return self.name

@app.route('/evaluate/<img_name>')
def evaluate(img_name):
    global truth_num
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

    ground_truth = labels[pos_highest]
    for i in img_rows:
        print(i.player)
        if i.label == ground_truth:
            print('true')
            Player.query.filter_by(name=i.player).first().rank += 10
        else:
            print('false')
            Player.query.filter_by(name=i.player).first().rank -= 10
    db.session.add(Truth(num=truth_num, name=img_name, truth=ground_truth))
    truth_num += 1
    db.session.commit()
    
    return '%s-%f' % (ground_truth, highest / count_sum)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)

