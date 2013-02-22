##
# @file server.py
# @Synopsis  supercontrol server
# @author jiaoew
# @version 0.2
# @date 2013-02-22

import logging
import tornado.ioloop
import tornado.web

from tornado.options import define, options
from tornado import database

#Global define
define("port", default=8887, help="run on the given port", type=int)

class MainHandler(tornado.web.RequestHandler):
	def get(self):
		self.write("hello world")
class VoteTemperatureHandler(tornado.web.RequestHandler):
	def post(self):
		#TODO
		self.write("cao");
# --------------------------------------------------------------------------
##
# @Synopsis  get location of the phone.
# ----------------------------------------------------------------------------
class LocationHandler(tornado.web.RequestHandler):
	def post(self):
		DELTA = 0.2
		lat = self.request.arguments["latitude"][0]
		lon = self.request.arguments["longitude"][0]
		logging.info("lat: %f, lon: %f", float(lat), float(lon))
		qr = []
		try:
			qr = db.query("SELECT * FROM `supercontrol`.`room` WHERE `latitude` > %s AND `latitude` < %s AND `longitude` > %s AND `longitude` < %s", str(float(lat) - DELTA), str(float(lat) + DELTA), str(float(lon) - DELTA), str(float(lon) + DELTA));
		except:
			logging.info("nothing")
			self.write("")
		rst = []
		for item in qr:
			logging.info(item)
			rst.append(item)
		self.write(tornado.escape.json_encode(rst))
class Application(tornado.web.Application):
	def __init__(self):
		handlers = [
			(r"/?", MainHandler),
			#(r"/temperature/get/?", TemperatureHandler),
			(r"/temperature/vote/?", VoteTemperatureHandler),
			(r"/location/?", LocationHandler),
		]
		settings = {}
		tornado.web.Application.__init__(self, handlers, **settings);
if __name__ == "__main__":
	tornado.options.parse_command_line()

	global db;
	db = database.Connection("localhost", "supercontrol", user = "controler", password = "hello world");
	#data = db.query("SELECT * FROM `supercontrol`.`room`");
	#for item in data:
		#logging.info(item)

	app = Application()
	app.listen(options.port)
	tornado.ioloop.IOLoop.instance().start()
