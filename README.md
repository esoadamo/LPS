LockPicker Simulator
===================
Hra LockPicker Simulator je hra p�esn� o tom, co jej� n�zev vypov�d�. Byla vytvo�ena pro program�torskou sout� [GeekWork 2016](http://www.itnetwork.cz/nezarazene/programatorska-soutez-geekwork-2016/hra-lock-picker-simulator) jako webov� hra, nicm�n� d�ky pou�it� jazyka Java je pln� (a mo�n� i l�pe) hrateln� jako desktopov� aplikace.


----------
## Vlastnosti ##
 1. Dynamicky generovan� prost�ed�
 2. Ak�nost - p�i maxim�ln�m FPS (p�edev��m v offline re�imu) se ze hry st�v� opravdu nap�nav� souboj s nep��telem a jeho taktikou, kde rychl� reflexy mohou zvr�tit celkov� v�sledek
 3. Taktika - Hra nab�z� mo�nost pe�liv� vymyslet svoji taktiku a rozdrtit nep��tele na pln� ���e
 4. Sout�ivost - vyhr�vejte/na�kupujte pomoc� vyhran� m�ny mnoho kosmetick�ch vylep�en�, kter� v�em uk��, �e vy jste tady ��f
 5. Vysok� rozli�en� - a� 4K, nicm�n� FPS krapet trp�
 6. Open source - po ukon�en� sout�e budou publikov�ny zdrojov� k�dy
 7. Web & Desktop klient - hrajte pohodln� bez p�ipojen� k internetu, nebo si u��vejte soupe�en� s ostatn�mi hr��i
 8. Nen�ro�n� server (ka�d� si jej m��e rozjet s minim�ln�mi n�klady)
 9. P��telsk� program�torsko-design�rsk� t�m ve slo�en� Adam Hlav��ek, Pavel R��i�ka, Tom� Hlav��ek
## P��b�h ##
###Historick� pozad�
Sir A'Lock Pick za��nal naprosto od p�ky. Brzy ale zjistil, �e lid� r�di zamykaj� v�ci, a tak za�al vyr�b�t z�mky. Jeho z�mky m�ly takovou kvalitu, �e netrvalo dlouho a jeho p�vodn� mal� obch�dek se stal nejv�t��m sv�tov�m obchodem se z�mky. Bohu�el takov� v�c tak� vyvolala necht�nou pozornost u r�zn�ch lapk� a zlod�j�, kte�� si cht�li tyto vysoce kvalitn� z�mky odn�st bez placen�.
###Vy
A proto vstupujete do hry vy - jste nezaj�mav� detektiv z nezaj�mav�ho m�sta v n�jak�m nespecifikovan�m st�t�, do kter�ho p�i�ly noviny s inzer�tem: "*Nejv�t�� obchodn� d�m se z�mky kv�li zv��en� kr�de�� hled� bezpe�nostn�ho zam�stance na pln� �vazek.*" Zaujalo V�s to a proto jste jel na pracovn� pohovor, kde jste se dozv�d�l v�ce o va�em �kolu: mus�te pozorovat a odhadnout, kter� z�mek se krimin�ln� �ivly pokus� ukr�st a zabr�nit jim v tom. Nen� to lehk� �kol, z�mk� je mnoho a vy jste jeden. Ale jste ten Jeden!
###Budocnoust
Budoucnost� se ji� zab�v� [jin� projekt](https://github.com/esoadamo/LPSGM)
##Instalace
###Samotn� desktopov� aplikace
Z [releases ](https://github.com/esoadamo/LPS/releases) st�hn�te nejnov�j�� *_client zip, rozbalte a spus�� LPS.jar.
###Server
 Z [releases ](https://github.com/esoadamo/LPS/releases) st�hn�te nejnov�j�� *_server zip, vytvo�te novou datab�zi lockpicker a importujte soubor lps.sql. Pot� na sv�j webov� server nahrajte lps.php (aplikace vyu��v� pro komunikaci knihovnu [MySQL2PHP2Java](https://github.com/esoadamo/MySQL2PHP2Java)) a nastavte podle instrukc� v souboru. T�m jste zprovoznili sv�j LPS server.
###Webov� aplikace
Pro spu�t�n� webov� aplikace se pou��v� naprosto ��asn� [Webswing](http://webswing.org/). P�edpokl�dejme, �e jste si st�hli nejnov�j�� *_client zip a um�stili jej do slo�ky LPS kter� je podslou�kou Webswing serveru.
P��klad konfigurace Webswing:
`...
{
    "name" : "LPS",
    "icon" : "data/coin.png",
    "mainClass" : "game.aho.lps.LockPickerSimulator",
    "classPathEntries" : [ "LPS.jar" ],
    "vmArgs" : "-Xmx200m -DauthorizedUser=${user}",
    "args" : "--web-browser --no-fullscreen --resolution 4",
    "homeDir" : "LPS",
    "maxClients" : 8,
    "antiAliasText" : true,
    "swingSessionTimeout" : 300,
    "authorization" : false,
    "isolatedFs" : false,
    "debug" : false,
    "authentication" : false,
    "directdraw" : true,
    "allowDelete" : false,
    "allowDownload" : false,
    "allowUpload" : false,
    "allowJsLink" : true
  },
  ...`

