##
# @file server.py
# @Synopsis  supercontrol server
# @author jiaoew
# @version 0.2
# @date 2013-02-22

import os
import logging
import tornado.ioloop
import tornado.web
#import torndb
import util

from tornado.options import define, options
from tornado import database

#Global define
define("port", default=8887, help="run on the given port", type=int)

def escape_date(list):
    for item in list:
        item['time'] = str(item['time'].replace(hour = item['time'].hour + 8))
    return tornado.escape.json_encode(list)
class MainHandler(tornado.web.RequestHandler):
    def get(self):
        self.write("hello world")
class TempRecordHandler(tornado.web.RequestHandler):
    def post(self):
        ROOM_LIMIT = 20
        roomId = self.request.arguments["roomId"][0]
        rst = []
        try:
            rst = db.query("SELECT temperature, time FROM `supercontrol`.`temp_record` WHERE rid = %s ORDER BY TIME DESC LIMIT 0, %s", str(roomId), ROOM_LIMIT)
        except Exception, e:
            logging.error("query temperature failed")
            self.write("failed")
            raise e
        a = escape_date(rst);
        logging.info(a);
        self.write(a)
class VoteHandler(tornado.web.RequestHandler):
    def post(self):
        roomId = self.request.arguments["roomId"][0]
        phoneId = self.request.arguments["phoneId"][0]
        temp = self.request.arguments["temperature"][0]
        voterId = phoneId
        #try:
            
        #except e:
            #raise e
            #logging.exception("vote select voterid error")
            #self.write("failed");
        num = [];
        try:
            db.execute("INSERT INTO `supercontrol`.`vote` (temperature, roomId, voterId) VALUES (%s, %s, %s)", str(temp), str(roomId), str(voterId))
            num = db.get("SELECT vote_num, temperature FROM `supercontrol`.`room` WHERE id = %s", str(roomId))
        except:
            logging.exception("vote selecte error")
            self.write("failed");
        logging.info(num)
        newtemp = (float(num['vote_num']) * float(num['temperature']) + float(temp)) / (float(num['vote_num']) + 1)
        try:
            if num['vote_num'] + 1 > util.MAX_VOTER:
                db.execute("UPDATE `supercontrol`.`room` SET temperature = %s, vote_num = %s WHERE id = %s", str(newtemp), util.MIN_VOTER, str(roomId))
                util.update_temp(roomId, newtemp)
            else:
                db.execute("UPDATE `supercontrol`.`room` SET temperature = %s WHERE id = %s", str(newtemp), str(roomId))
        except Exception, e:
            raise e
            logging.error("vote update tmperature error")
            self.write("failed");
        self.write("success");
# --------------------------------------------------------------------------
##
# @Synopsis  get location of the phone.
# ----------------------------------------------------------------------------
class LocationHandler(tornado.web.RequestHandler):
    def get(self):
        qr = []
        try:
            qr = db.query("SELECT b.*, a.temperature as old_temp FROM `supercontrol`.`room` b join `supercontrol`.`temp_record` a on b.id = a.rid WHERE time in (SELECT max(time) from `temp_record` WHERE rid = a.rid)")
        except:
            logging.info("query error")
            self.write("failed")
        print qr
        self.write(tornado.escape.json_encode(qr))
    def post(self):
        DELTA = 0.01
        lat = self.request.arguments["latitude"][0]
        lon = self.request.arguments["longitude"][0]
        logging.info("lat: %f, lon: %f", float(lat), float(lon))
        qr = []
        try:
            qr = db.query("SELECT b.*, a.temperature as old_temp FROM `supercontrol`.`room` b join `supercontrol`.`temp_record` a on b.id = a.rid WHERE time in (SELECT max(time) from `temp_record` WHERE rid = a.rid and b.latitude > %s AND b.latitude < %s AND b.longitude > %s AND b.longitude < %s)", str(float(lat) - DELTA), str(float(lat) + DELTA), str(float(lon) - DELTA), str(float(lon) + DELTA));
        except:
            logging.info("nothing")
            self.write("")
        #rst = []
        #for item in qr:
            #logging.info(item)
            #rst.append(item)
        print qr
        self.write(tornado.escape.json_encode(qr))
class Application(tornado.web.Application):
    def __init__(self):
        handlers = [
                (r"/?", MainHandler),
                #(r"/temperature/get/?", TemperatureHandler),
                (r"/vote/?", VoteHandler),
                (r"/location/?", LocationHandler),
                (r"/record/?", TempRecordHandler),
                ]
        settings = {}
        tornado.web.Application.__init__(self, handlers, **settings);
if __name__ == "__main__":
    tornado.options.parse_command_line()

    global db;
    db = database.Connection("localhost", "supercontrol", user = "controler", password = "hello world");
    rst = db.query("SELECT temperature, time FROM `supercontrol`.`temp_record` WHERE rid = %s ORDER BY time DESC LIMIT 0, %s", 8, 10)
    #logging.info(rst)
    logging.info(escape_date(rst))
    #num = db.get("SELECT vote_num, temperature FROM `supercontrol`.`room` WHERE id = %s", str(5))
    #logging.info(num['vote_num'])
    #logging.info(num['temperature'])
    #data = db.query("SELECT * FROM `supercontrol`.`room`");
    #for item in data:
        #logging.info(item)

    app = Application()
    app.listen(options.port)
    tornado.ioloop.IOLoop.instance().start()
    os.system("python util.py")
    #updateTempTimer(db)
