'''
Created on 2016. 1. 3.

@author: simpson swhors@naver.com
@file: migondb.py
'''

from google.appengine.ext import ndb

XML_HEADER= "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
XML_TOP="<list>\n"
XML_BOTTOM="</list>\n"
XML_DATACOUNT="<datacount>%s</datacount>\n"
XML_FORMAT='<goods num=\'%d\'>\n<title>%s</title>\n<id>%s</id>\n<date>%s</date>\n<count>%s</count>\n<url>%s</url>\n<image>%s</image>\n<mallname>%s</mallname>\n<price>%s</price>\n</goods>\n'
TIME_FORMAT="%d %b %Y %H:%M:%S GMT"

DEFAULT_DB_NAME = 'GoodsInfoNdb'

class GoodsInfoNdb(ndb.Model):
    title=ndb.StringProperty()
    date = ndb.DateTimeProperty(auto_now=True,auto_now_add=True)
    count=ndb.IntegerProperty()
    goodsid=ndb.IntegerProperty()
    id=ndb.IntegerProperty()
    age=ndb.IntegerProperty()
    url=ndb.StringProperty()
    image=ndb.StringProperty()
    mallname=ndb.StringProperty()
    price=ndb.IntegerProperty()

def deleteAllData(aHandle):
    sQuery = GoodsInfoNdb.query()
    sResultList = sQuery.fetch()
    for sResult in sResultList:
        sResult.key.delete()
    
def deleteData(aHandle, aTitle, aId):
    sKey = ndb.Key(DEFAULT_DB_NAME, aId)
    sKey.delete()
            
def getStoredDataCount(aHandle, aTitle, aId):
    sQuery = GoodsInfoNdb.query(GoodsInfoNdb.title==aTitle,  
                    GoodsInfoNdb.goodsid==int(aId)).order(GoodsInfoNdb.date)
    sDataCount = sQuery.count()
    return sDataCount
    
def selectData(aHandle, selectAllData=True, aLimit=0, aId=""):
    sReadedDataCount = 0
    if selectAllData == True:
        sQuery = GoodsInfoNdb.query().order(-GoodsInfoNdb.date)
    else:
        sQuery = GoodsInfoNdb.query(GoodsInfoNdb.goodsid==int(aId)).order(GoodsInfoNdb.date)
    
    sDataCount = sQuery.count()
    aHandle.response.out.write(XML_HEADER)
    aHandle.response.out.write(XML_TOP)
    aHandle.response.out.write(XML_DATACOUNT % sDataCount)

    if sDataCount > 0:
        if aLimit > 0:
            sResultList = sQuery.fetch(aLimit)
        else:
            sResultList = sQuery.fetch()
        for goodsinfo in sResultList:
            sReadedDataCount += 1
            sGoodsInfoCount = 0
            updateDate = goodsinfo.date.strftime(TIME_FORMAT)
            
            if goodsinfo.count != None:
                sGoodsInfoCount = goodsinfo.count
            
            aHandle.response.out.write(XML_FORMAT
                                        % (sReadedDataCount,
                                           goodsinfo.title,
                                           goodsinfo.goodsid,
                                           updateDate,
                                           sGoodsInfoCount,
                                           goodsinfo.url,
                                           goodsinfo.image,
                                           goodsinfo.mallname,
                                           goodsinfo.price))
    aHandle.response.out.write(XML_BOTTOM)
    return sDataCount

def updateDataCount(aHandler, aTitle, aId):
    sGoodsInfoCount = 0
    sDataCount = 1
    sQuery = GoodsInfoNdb.query(GoodsInfoNdb.goodsid==int(aId), GoodsInfoNdb.title==aTitle)
    if sQuery.count() > 0:
        sResultList = sQuery.fetch(1)
        sGoodsInfo = sResultList[0]
        sGoodsInfoCount = sGoodsInfo.count 
        sGoodsInfo.count += 1
        sGoodsInfo.put()
        aHandler.response.out.write(XML_HEADER)
        aHandler.response.out.write(XML_TOP)
        aHandler.response.out.write(XML_DATACOUNT % sDataCount)
        aHandler.response.out.write(XML_BOTTOM)

def insertData(aHandler, aTitle, aId, aAge, aUrl, aImage, aMallName, aPrice):
    sDataCount = 1
    sDB = GoodsInfoNdb(title   = aTitle,
                       id      = 0,
                       goodsid = int(aId),
                       age     = int(aAge),
                       count   = 1,
                       url     = aUrl,
                       image   = aImage,
                       mallname= aMallName,
                       price   = int(aPrice))
    sDB.key = ndb.Key(DEFAULT_DB_NAME, aId)
    sDB.put()
    aHandler.response.out.write(XML_HEADER)
    aHandler.response.out.write(XML_TOP)
    aHandler.response.out.write(XML_DATACOUNT % sDataCount)
    aHandler.response.out.write(XML_BOTTOM)
