[![Scc Count Badge](https://sloc.xyz/github/klemek/autologin/?category=code)](https://github.com/boyter/scc/#badges-beta)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Klemek/AutoLogin.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Klemek/AutoLogin/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/Klemek/AutoLogin.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Klemek/AutoLogin/alerts/)

# Auto Login
Attempt to login to a known firewall if there is no internet connection

Use provided (inside jar) phantomJS binary or take its path as first argument.

## Configuration

`config.properties` (in working directory) format :
```
address=(firewall url)
page_title=(title of the page, leave blank to not check)
username_field_id=(DOM id if the username field)
pass_field_id=(DOM id if the password field)
button_id=(DOM id if the button)
login=(login to use, leave blank to ask)
pass=(password to use, leave blank to ask)

# Connection tests configuration, these values (without comments) should work
test_ports=80,8080 (ports to check, leave blank to skip)
test_address=http://portquiz.net (url to reach, leave blank to skip)
test_head=<html>\n<head>\n<title>Outgoing Port Tester</title> (starting of the response content)
```

## Downloads
* [Any platform (94 MB)](https://raw.githubusercontent.com/Klemek/AutoLogin/master/download/autologin-1.0.jar)
* [Linux 32bit (34 MB)](https://raw.githubusercontent.com/Klemek/AutoLogin/master/download/autologin-1.0-linux32.jar)
* [Linux 64bit (33 MB)](https://raw.githubusercontent.com/Klemek/AutoLogin/master/download/autologin-1.0-linux64.jar)
* [Windows (25 MB)](https://raw.githubusercontent.com/Klemek/AutoLogin/master/download/autologin-1.0-windows.jar)
* [MacOSX (24 MB)](https://raw.githubusercontent.com/Klemek/AutoLogin/master/download/autologin-1.0-macosx.jar)
* [No phantomJS (8 MB)](https://raw.githubusercontent.com/Klemek/AutoLogin/master/download/autologin-1.0-light.jar)
