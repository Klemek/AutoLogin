# Auto Login
attempt to login to a firewall if there is no internet connection

Uses PhantomJS, first argument is path of binary otherwise it will extract it in a temporary folder

`config.properties` format :
```
address=(firewall address)
page_title=(title of the page, leave blank to not check)
username_field_id=(DOM id if the username field)
pass_field_id=(DOM id if the password field)
button_id=(DOM id if the button)
login=(login to use, leave blank to ask)
pass=(password to use, leave blank to ask)

test_ports=80,8080 (ports to check, leave blank to skip)
test_address=http://portquiz.net (url to reach, leave blank to skip)
test_head=<html>\n<head>\n<title>Outgoing Port Tester</title> (starting of the response content)
```