from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import parse_qs
import urllib.request
import json

class S(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()

    def do_POST(self):
        content_length = int(self.headers['Content-Length'])
        body = self.rfile.read(content_length)
        query = json.loads(body)
        res = searchDB(query["keyword"])
        print(query["keyword"])
        data = json.dumps(res).encode()
        self._set_headers()
        self.wfile.write(data)


def run(server_class=HTTPServer, handler_class=S, port=8080):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print('Starting http port ' + str(port))
    httpd.serve_forever()


def searchDB(keyword):
    url = 'http://localhost:9200/_all/_search?q=text:' + str(keyword)
    f = urllib.request.urlopen(url)
    data = json.loads(f.read().decode('utf-8'))
    return data

if __name__ == "__main__":
    from sys import argv

    if len(argv) == 2:
        run(port=int(argv[1]))
    else:
        run()

