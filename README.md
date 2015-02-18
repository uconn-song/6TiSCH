# SeniorDesign-WirelessHART
# September 2014 - May 2015

WSNManager is the primary project.  SerialReader was the original code to get started and it was redesigned into WSNManager.

Windows Dependencies  
WinPCap installation for JNetPCap  
jnetpcap.dll -> System32 folder for JNetPcap  


Linux Dependencies  
Uknown


TODO:  
Have network manager able to connect to internet (start a server and listen for input from web)  
Test network connectivity, ensure that messages are heard coming from internet (copper plugin to send messages for example, print to std out)  
Formalize connection between DAG root and server (currently just making a fake network)  
Set up 6lowpan header compression/decompression on WSNManager side  
Use CoAP to send a message to WSNManager  
have JNetPCap parse and send packets  
Forward CoAP message from WSNManager to mote in network  
Write FW-Application to listen for coap messages and give back response, echo will do for a start  
Write FW-Application to modify neighbor table and schedule  
Write WSNManager software to visualize network  
Write WSNManager software to modify network parameters  
