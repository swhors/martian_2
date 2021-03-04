'''
Created on 2016. 1. 3.

@author: simpson swhors@naver.com
@file: migoparseopcode.py
'''

from migondb import getStoredDataCount
from migondb import insertData
from migondb import updateDataCount
from migondb import deleteData
from migondb import deleteAllData
from migondb import selectData

##########################################################
#Definition of OP CODE
STR_OP_CODE_INSERT      ='insert'
STR_OP_CODE_SELECT      ='select'
STR_OP_CODE_SELECTALL   ='selectall'
STR_OP_CODE_SELECTLATEST='selectlatest'
STR_OP_CODE_DELETE      ='delete'
STR_OP_CODE_DELETEALL   ='deleteall'
STR_OP_CODE_UPDATE      ='update'
STR_OP_CODE_UNSUPPORTED ='unsupported'

def processInsert(aHandle):
    sTitle = aHandle.request.GET['title']
    sID    = aHandle.request.GET['id']
    sCount = getStoredDataCount(aHandle, sTitle, sID )
    if sCount == 0:
        sUrl   = (aHandle.request.GET['url']).replace('\"', '')
        sImage = (aHandle.request.GET['image']).replace('\"', '')
        insertData(aHandle,
                   sTitle,
                   sID,
                   aHandle.request.GET['age'],
                   sUrl,
                   sImage,
                   aHandle.request.GET['mallname'],
                   aHandle.request.GET['price'])
    else:
        updateDataCount(aHandle,
                        sTitle,
                        sID )

def processSelect(aHandle):
    selectData(aHandle,
               False,
               5,
               aHandle.request.GET['id'])

def processSelectAll(aHandle):
    selectData(aHandle)

def processSelectLatest(aHandle):
    selectData(aHandle,
               True,
               5)

def processDelete(aHandle):
    deleteData(aHandle,
               aHandle.request.GET['title'],
               aHandle.request.GET['id'] )
    aHandle.response.write('<html><body>')
    aHandle.response.write("<h1>delete</h1>")
    aHandle.response.write('</body></html>')

def processDeleteAll(aHandle):
    deleteAllData(aHandle)
    
def processUpdate(aHandle):
    aHandle.response.write('<html><body>')
    aHandle.response.write("<h1>update</h1>")
    aHandle.response.write("<h3>not yet supported.</h3>")
    aHandle.response.write('</body></html>')

def processUnsupported(aHandle):
    aHandle.response.write('<html><body>')
    aHandle.response.write("<h1>Unsupported : %s</h1>" %
                           aHandle.request.GET['opcode'])
    aHandle.response.write('</body></html>')

gClassProc = {STR_OP_CODE_INSERT:processInsert,
              STR_OP_CODE_SELECT:processSelect,
              STR_OP_CODE_SELECTALL:processSelectAll,
              STR_OP_CODE_SELECTLATEST:processSelectLatest,
              STR_OP_CODE_DELETE:processDelete,
              STR_OP_CODE_DELETEALL:processDeleteAll,
              STR_OP_CODE_UPDATE:processUpdate,
              STR_OP_CODE_UNSUPPORTED:processUnsupported}
