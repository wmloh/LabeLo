import json
import numpy as np
from server import db, Label

query_id = db.session.query(Label.id).all()
query_label = db.session.query(Label.label).all()

processed = []

for i,j in zip(query_id, query_label):
    processed.append((i,j))

print(processed)

def empty(db):
    db.drop_all()
    db.create_all()
