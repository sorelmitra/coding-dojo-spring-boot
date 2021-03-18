from verifit import *
import re

server = "192.168.99.102"
port = 8770

#server = "localhost"
#port = 8080

def test_US_Mountain_View():
	name = "basic_US_Mountain_View"

	command = [
		"curl", 
		"-X", "POST",
		f"http://{server}:{port}/weather",
		"--data", f"city=Mountain%20View",
		"-o", get_output_filename(name)
	]

	expected, got = run_test(command, name)
	assert None != re.match(expected, got)
