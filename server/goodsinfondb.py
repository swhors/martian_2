'''
Created on 2016. 1. 3.

@author: simpson swhors@naver.com
@file: goodsinfondb.py
'''

from migoparseopcode import gClassProc
from migoparseopcode import STR_OP_CODE_UNSUPPORTED

import webapp2

class MainPage(webapp2.RequestHandler):
    def get(self):
        sOpcode=self.request.GET['opcode']
        if sOpcode in gClassProc:
            gClassProc[sOpcode](self)
        else:
            gClassProc[STR_OP_CODE_UNSUPPORTED](self)

app = webapp2.WSGIApplication([
    ('/goods.cgi', MainPage)], debug=True)