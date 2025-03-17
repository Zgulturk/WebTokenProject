from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import sqlite3
import requests

app = FastAPI()

# Kullanıcı Modeli
class User(BaseModel):
    username: str
    password: str

# Basit SQLite veritabanı oluşturma
def init_db():
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS users (
                      id INTEGER PRIMARY KEY, 
                      username TEXT UNIQUE, 
                      password TEXT)''')
    conn.commit()
    conn.close()

init_db()

# Kullanıcı kimlik doğrulama
@app.post("/login")
def login(user: User):
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM users WHERE username=? AND password=?", (user.username, user.password))
    result = cursor.fetchone()
    conn.close()

    if result:
        # Java servisinden JWT token al
        response = requests.post("http://localhost:8080/generate-token", json={"username": user.username})
        if response.status_code == 200:
            return {"token": response.json()["token"]}
        else:
            raise HTTPException(status_code=500, detail="Token alınamadı")
    else:
        raise HTTPException(status_code=401, detail="Geçersiz kullanıcı adı veya şifre")
