import json
import numpy as np
from server import db, Label

query_id = db.session.query(Label.id).all()
query_name = db.session.query(Label.name).all()
query_label = db.session.query(Label.label).all()

processed = []

for i,j,k in zip(query_id, query_name, query_label):
    processed.append((i,j,k))

print(processed)

def empty(db):
    db.drop_all()
    db.create_all()
