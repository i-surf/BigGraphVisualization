from http.server import BaseHTTPRequestHandler, HTTPServer  
from urllib.parse import parse_qs
import urllib.request
import json

#클래스 S는 BaseHTTPRequestHandler를 상속받은 클래스다. 
class S(BaseHTTPRequestHandler):  #http의 기본적인 응답을 받을 수 있는 기능 
    def _set_headers(self):
        self.send_response(200) #응답코드 
        self.send_header('Content-type', 'application/json') #응답헤더
        self.end_headers() #헤더와 본문을 구분하는 메소드 (쓰지않으면 본문이 몽땅 헤드로 들어가 에러발생)

    #BaseHTTPRequestHandler클래스의 do_POST() 메소드를 오버라이딩한 것. post요청이 들어오면 이 메소드가 호출됨
    def do_POST(self): 
        content_length = int(self.headers['Content-Length'])
        body = self.rfile.read(content_length) #요청 body 읽기 
        #body : b'{"keyword" : "playground"}'

        query = json.loads(body)
        #query : {'keyword': 'playground'}
        print(query["keyword"]) #쿼리 키워드 출력 

        res = searchDB(query["keyword"])
        #searchDB의 결과이므로 최종결과 형태 [ {'title': 'test', 'link' : 'test'}, {'title': 'test', 'link' : 'test'}]

        data = json.dumps(res).encode()
        #data : b' + res 형태 

        #응답을 보내는 부분
        self._set_headers()
        self.wfile.write(data) #메시지 텍스트가 구성된 후, 응답소켓을 감싸는 파일 핸들 wfile에 기록된다. 

def run(server_class=HTTPServer, handler_class=S, port=8080): #서버 객체 생성시 핸들을 정한다. 
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print('Starting http port ' + str(port))
    httpd.serve_forever() #run하면 이 메소드를 통해 서버는 요청을 기다린다. 요청이 들어오면 등록된 핸들러에 그 정보를 전달한다. 

def searchDB(keyword):
    url = 'http://localhost:9200/index17/_search?q=text:' + str(keyword)
    #url = 'http://localhost:9200/index17/_search?q=text:' + str(keyword) + ''' -d '{"_source": ["title", "link"]}' '''
    #url = 'http://localhost:9200/_all/_search?size=1000000&q=text:' + str(keyword)
    
    f = urllib.request.urlopen(url)
    data = json.loads(f.read().decode('utf-8'))["hits"]["hits"]
    #print(data)

    #extract title and link from each document in data
    res = [] 
    for i in range(len(data)): # run every inner hits
        node = {}
        node["title"] = data[i]["_source"]["title"]
        node["link"] = data[i]["_source"]["link"]
        res.append(node)

    print(res)
    return res

if __name__ == "__main__":
    from sys import argv

    if len(argv) == 2:
        run(port=int(argv[1]))
    else:
        run()
