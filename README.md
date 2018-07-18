# june.music.mm--沫沫音乐播放器(MoMoPlayer)

![MoMoPlayer音乐播放器](https://github.com/junehappylove/img_lib/blob/master/momoplayer/image2.png "新版程序界面图片")
![MoMoPlayer音乐播放器选项](https://github.com/junehappylove/img_lib/blob/master/momoplayer/image3.png "新版程序选项界面")

# 开发日志

- 2018.07.18 关于项目打包说明，参考wiki
- 2018.07.18 目前仅剩下examples、jspeex、kj_dsp这三个jar在maven仓库中找不到了
- 2018.07.18 项目修改为maven项目，去掉无用的jar包
- 2018.07.18 修复PlayList的分隔栏颜色，同PlayList的整体背景色
- 2017.03.26 MoMoPlayer项目更名为 *june.music.mm*

# 版本

## 2.0.1 [2017-01-12] 

>1. 精简源码，去掉com.ctreber.aclib.*包,这个包是处理ico文件的一个库，重新引入这个jar包[`aclibico-2.1.jar`](https://sourceforge.net/projects/aclibico/files/aclibico/2.1/)
>2. 修改歌曲文件读入标签的一些错误处理。

## 2.0 [2017-01-03] 升级版本至2.0

>1. 修改版本升级方法;
>2. 修改用户留言地址。

## 1.3.1 对包com.judy.audiotag.*包重构[2017-01-03]

>1. 修改了代码大量的错误和警告;
>2. 完善程序更新代码.

## 1.3最新的修改代码[2016-10-22]

>1. 最新的歌词搜索，原版中使用千千静听，和百度音乐搜索都不好使了，最新采用[`歌词迷`](http://doc.gecimi.com/en/latest/#)的歌词搜索；
>2. 屏蔽掉联网更新功能，因为国内访问google不了，同时原项目也不在维护了（momoplayer修改来自`千里冰封`的yoyoplayer）
 
## 1.2更新[2009-08-02]

>1. 全新的歌词搜索方式，使搜索率大大提高。
>2. 把整个代码全部移到google code上去。以前的sourceforge和java.net已经不再维护了。

## 1.1.3更新[2008-07-28]

>1. 完全去除了其它的搜索部分,因为有些网页格式经常会变动,所以一段时间以后可能又搜不到歌词了,现在搜索歌词用的是百度的filetype:lrc 歌曲名-歌手名 的方式进行搜索.
>2. 更新了在线搜索歌词的时候,只能下载一首的情况,以前点了下载之后,整个界面就不可用了,要重新输入歌曲信息才可用,现在已经不做限制了.
>3. 修补了当以前下载并匹配的歌词文件不见的时候,会出现既不下载新的歌词也不提示出错,现在当以前的歌词文件不存在时,会自动上网搜索新的歌词.

## 1.1.2更新[2008-04-03]

>1. 更改了精品网页的歌词搜索部分,因为精品网页的内容发生了改变,如果不更改对应的搜索方式的话将不能从精品网页上查询到歌词,无论怎么样,都对精品网页表示感谢,它的网址是:http://www.jpwy.net/gc
>2. 修正了程序在某些LINUX版本上的系统上打开会抛出异常而影响程序使用的BUG.
>3. 在歌词秀窗口去掉了作者的名字:),此前的不便,望各位用户原谅.
>4. 在使用过程中,有任何的建议,可以加作者的QQ:980154978,注明MoMoPlayer,或者到http://blog.csdn.net/junehappylove/article/details/52850828 上留言,此博客为作者的博客,再此感谢大家对MoMoPlayer的支持.

## MOMOPlayer1.0版推出之后,得到了一些朋友的认可和反馈,经过这段时间的修改,现在推出MOMOPlayer1.1版.相对于1.0版,改进的地方如下:

### 一. 设置方面
>1. 增加了首选项的功能,很多方面现在已经可以配置

### 二. 歌词方面
>1. 增加了歌词搜索的自主性,用户可以选择自定义搜索关键字,也可以自定义是否和歌词文件相关联,这里的关联并不是把歌词写入歌曲文件,而是在程序中建立一种关联,以免每次播放这首歌的时候都去搜索歌词,而是直接去读取关联好的歌词文件.
>2. 增加了指定歌词的搜索目录,指定歌词的保存目录功能.为了性能考虑,歌词的搜索目录不会往下递归,只搜索设置的那一级目录.
>3. 歌词的设置更为丰富,很多地方以前是固定的,现在都是可以改变的,并且增加了纵向显示歌词的卡拉OK显示功能,还支持字体是否抗锯齿显示的选项.
>4. 支持淡入淡出显示歌词,支持多种对齐方式.
>5. 在windows下面支持歌词窗口的透明显示.在linux下面,此选项将被禁用:(

### 三. 标签方面
>1. 现在完全支持ID3v1,ID3v2,APEv2,Vorbis标签的读取和写入,并可设置标签的读取和写入的编码.以免在读取标签的时候出现乱码的情况.程序的默认设置编码是GBK,如果是UTF-8的用户,可以在首选项修改.

### 四. 其它方面
>1. 可以选择音频设备,这在一定程度上可以选择几个设置对比一下效果.尽量减少播放器没有声音的情况.
>2. 增加在任务栏滚动显示歌曲标题.
>3. 支持设置代理服务器连接网络.
>4. 支持对播放列表进行一些细化的设置,因为程序预设的颜色不一定符合大家的审美观.
>5. 配置目录现在改为{user.home}/.MOMOPlayer/,默认的歌词目录是{user.home}/.MOMOPlayer/Lyrics/
>6. 快捷键:C:播放,V:停止,B:下一首,空格:暂停,F2:显示/隐藏歌词秀,F3:显示/隐藏EQ窗口,F4:显示/隐藏播放列表
>7. 在一定程度上实现了窗口的吸附的功能,虽然不是很完美,还望各位有什么好的想法一起分享:)

### 在首选项里在的还有很多功能没有实现...

MOMOPlayer现在项目已经移至github，以前的sourceforge和java.net上面的项目不再维护了(国内的速度很慢)。    
github上主页的地址是：https://github.com/junehappylove/MoMoPlayer    
在程序的使用过程中有任何意见和BUG反应，请到 https://github.com/junehappylove/MoMoPlayer/issues/list 上新建issue。    
在使用的过程中,有任何问题,可以加本人的*QQ:980154978*.希望和大家一起探讨JAVA问题,让JAVA的浓香飘的更远.    
最后,谢谢我自己，不知道是什么动力让我取维护这个项目，哈哈。
 
# 关于程序中乱码问题

1. 在ubuntu8.04下面整个界面都是乱码.  
 
这不是程序的问题,而是由于在ubuntu 8.04里uming.ttf变成了uming.ttc,   
而ubuntu里java默认的中文字体就是uming.ttf,   
所以只要获得它就可以了，可以运行如下命令:

```shell
	sudo ln -s /usr/share/fonts/truetype/arphic/uming.ttc \ /usr/share/fonts/truetype/arphic/uming.ttf
```

运行成功以后,就可以正常显示JAVA的字体了.
   
2. 歌词乱码或者歌曲信息乱码问题.
 
目前歌词全部是从baidu上面搜索的,统一使用了GBK的编码方式.   
歌曲信息是用ID3v1,ID3V2,APEv2的格式来读取的,可以设置这三种标签的读取顺序.   
并且可以设置ID3v1的编码,默认我用的是GBK,在linux下面,可能要改成UTF-8.   
标准的APEv2都是UTF-8编码来存储歌曲信息的,所以不会出现乱码问题,推荐使用APEv2标签来保存歌曲信息.

## 有任何其它的问题,欢迎联系作者: 
### QQ:*980154978*
### 邮件:*980154978@qq.com*
### BLOG:[http://blog.csdn.net/junehappylove](http://blog.csdn.net/junehappylove,'CSDN博客:junehappylove')

[image3]:https://github.com/junehappylove/img_lib/blob/master/momoplayer/image3.png "新版程序界面图片"
[image2]:https://github.com/junehappylove/img_lib/blob/master/momoplayer/image2.png "新版程序界面图片"
[image1]:https://github.com/junehappylove/img_lib/blob/master/momoplayer/MoMoPlayer%E9%9F%B3%E4%B9%90%E6%92%AD%E6%94%BE%E5%99%A8.jpg "老版程序界面图片"