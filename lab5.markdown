---
layout: default
title: Lab 5 - Basic Encryption
---	

## Lab
	
In this lab we will extract some sensitive information from the
filesystem of the device.  In this case the information we need will
not be encrypted, and we simply need to find it.

Connect to the emulator with `adb shell`.  You will be given shell
access to the Android emulator.Browse to the data store of the app.

{% highlight console %}
(server-env)sartre:AndroidLabs2 maxim$ adb shell
# cd /data/data/com.securitycompass.androidlabs.base
# ls
lib
shared_prefs
# cd shared_prefs
# ls
preferences.xml
com.securitycompass.androidlabs.base_preferences.xml
{% endhighlight %}

The information we want is in the preferences.xml file
{% highlight console %}
# cat preferences.xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
<string name="serverpass">password</string>
<string name="localpasssalt">+tm+vXQuNL01T2caEwlG6XBj9ZrS9w5XfVk5EFV15SQ=
</string>
<string name="serveruser">jdoe</string>
<boolean name="firstrun" value="false" />
<string name="localpasshash">vt8O7P2Y1dPYjRJG/F7QXADtpc2/DxlvpYya2b/oSIA=
</string>
</map>
{% endhighlight %}

As you can see, the username and password are stored in the strings
`serveruser` and `serverpass` (these are out of order, but present in
the XML file).  

## Solution

We want to find some way of 
