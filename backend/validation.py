import json
import numpy as np
from server import db, Label, Player, url
import requests

def display():
    query_id = db.session.query(Label.id).all()
    query_name = db.session.query(Label.name).all()
    query_label = db.session.query(Label.label).all()
    query_player = db.session.query(Label.player).all()

    processed = []
    print('----Labels----')
    for i,j,k,l in zip(query_id, query_name, query_label, query_player):
        processed.append((i,j,k,l))

    print(processed)

    print('----Player----')
    player_id = db.session.query(Player.id).all()
    player_name = db.session.query(Player.name).all()
    player_correct = db.session.query(Player.correct).all()
    player_wrong = db.session.query(Player.wrong).all()
    player_rank = db.session.query(Player.rank).all()

    players = []

    for i,j,k,l,m in zip(player_id, player_name, player_correct, player_wrong, player_rank):
        players.append((i,j,k,l,m))

    print(players)

def empty(db):
    db.drop_all()
    db.create_all()

if __name__ == '__main__':
    display()
