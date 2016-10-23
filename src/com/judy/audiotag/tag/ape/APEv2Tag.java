/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.audiotag.tag.ape;

import com.judy.audiotag.tag.FieldDataInvalidException;
import com.judy.audiotag.tag.KeyNotFoundException;
import com.judy.audiotag.tag.Tag;
import com.judy.audiotag.tag.TagField;
import com.judy.audiotag.tag.TagFieldKey;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author judy
 */
public class APEv2Tag implements Tag {

    /**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = 8816269667826600599L;
	private static Logger log = Logger.getLogger(APEv2Tag.class.getName());
    private File input;
    private TagHead head;
    private TagBody body;
    private String artist = "";
    private String album = "";
    private String title = "";
    private String year = "";
    private String comment = "";
    private String track = "";
    private String genre = "";
    @SuppressWarnings("unused")
	private int fieldCount;
    private Map<String, String> map;

    public APEv2Tag(File file) throws IOException, UnsupportedAudioFileException {
        this.input = file;
        map = new HashMap<String, String>();
        load();
    }

    public APEv2Tag() {
        map = new HashMap<String, String>();
    }

    protected void load() throws IOException, UnsupportedAudioFileException {
        RandomAccessFile raf = new RandomAccessFile(input, "r");
        //先查看最后32个字节
        try {
            raf.seek((int) (input.length() - 32));
            byte[] buffer = new byte[32];
            raf.read(buffer);
            head = new TagHead(buffer);
            if (head.isValid()) {
                log.log(Level.INFO, "读取:最后32个字节有标签!");
                int size = head.getTagSize();
                raf.seek((int) (input.length() - size));
                buffer = new byte[size - 32];
                int read = 0;
                while (read < buffer.length) {
                    read += raf.read(buffer, read, buffer.length - read);
                }
                body = new TagBody(buffer);
                List<TagItem> list = body.getItems();
                for (TagItem item : list) {
                    log.log(Level.INFO, item.toString());
                }

            } else {//再查看128前面的32个字节
                raf.seek((int) (input.length() - 32 - 128));
                raf.read(buffer);
                head = new TagHead(buffer);
                if (head.isValid()) {
                    log.log(Level.INFO, "读取:ID3v1前面的字节有标签!");
                    int size = head.getTagSize();
                    raf.seek((int) (input.length() - size - 128));
                    buffer = new byte[size - 32];
                    int read = 0;
                    while (read < buffer.length) {
                        read += raf.read(buffer, read, buffer.length - read);
                    }
                    body = new TagBody(buffer);
                    List<TagItem> list = body.getItems();
                    for (TagItem item : list) {
                        log.log(Level.INFO, item.toString());
                    }

                } else {
                    throw new UnsupportedAudioFileException("读取:找不到APEv2格式的标签!");
                }
            }
        } finally {
            try {
                raf.close();
                readTag();
            } catch (Exception exe) {
                throw new UnsupportedAudioFileException("读取:找不到APEv2格式的标签!");
            }
        }
    }

    private void readTag() {
        for (TagItem item : body.getItems()) {
            map.put(item.getId(), item.getContent());
        }
        this.album = map.get(APEv2FieldKey.Album.name());
        this.artist = map.get(APEv2FieldKey.Artist.name());
        this.comment = map.get(APEv2FieldKey.Comment.name());
        this.genre = map.get(APEv2FieldKey.Genre.name());
        this.title = map.get(APEv2FieldKey.Title.name());
        this.track = map.get(APEv2FieldKey.Track.name());
        this.year = map.get(APEv2FieldKey.Year.name());
    }

    protected List<TagField> returnFieldToList(TagItem field) {
        List<TagField> fields = new ArrayList<TagField>();
        fields.add(field);
        return fields;
    }

    /**
     * 写出APE标签到文件里面去
     * @param raf 随机文件流
     * @param hasID3v1 是否有ID3v1标签
     * @throws java.io.IOException
     */
    public void write(RandomAccessFile raf, boolean hasID3v1) throws IOException {
        //如果有ID3标签,则先把它缓存起来,总共128个字节
        byte[] temp = null;
        int deleteLength = 0;
        if (hasID3v1) {
            temp = new byte[128];
            raf.seek(raf.length() - 128);
            raf.read(temp);
            deleteLength += 128;
        }
        TagHead header = checkTag(raf);
        //如果有标头,则说明有APE的标签,还要多删一些
        if (header != null) {
            log.log(Level.INFO, "原来存在APEv2标签,先删除之...");
            int length = header.getTagSize();
            if (header.hasHeader()) {//如果有标头的话,长度还要加32个字节
                length += 32;
            }
            deleteLength += length;
        } else {
            log.log(Level.INFO, "以前不存在APEv2标签,直接添加...");
        }
        raf.setLength(raf.length() - deleteLength);
        //把该截掉的都截了以后,就开始写标签了,先写APE的,再看
        //有没有ID3的,有就写,没有就不写了
        raf.seek(raf.length());
        byte[] data = getTagBytes();
        raf.write(data);
        if (temp != null) {
            raf.write(temp);
        }
        log.log(Level.INFO, "APEv2标签写出完毕...");
    }

    /**
     * 得到标签所代表的字节数组
     * @return 标签所代表的字节数组
     */
    private byte[] getTagBytes() throws UnsupportedEncodingException, IOException {
        int itemCount = map.size();
        body = new TagBody();
        for (Map.Entry<String, String> en : map.entrySet()) {
            body.addTagItem(new TagItem(en.getKey(), en.getValue()));
        }
        byte[] bodyData = body.getBytes();
        log.log(Level.SEVERE, "BODYSIZE=" + bodyData.length);
        TagHead header = new TagHead();
        header.setFlag(TagHead.HEAD);
        header.setItemCount(itemCount);
        header.setTagSize(bodyData.length + 32);
        header.setVersion(TagHead.V2);

        TagHead foot = new TagHead();
        foot.setFlag(TagHead.FOOT);
        foot.setItemCount(itemCount);
        foot.setTagSize(bodyData.length + 32);
        foot.setVersion(TagHead.V2);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bout.write(header.getBytes());
        bout.write(bodyData);
        bout.write(foot.getBytes());
        bout.flush();
        return bout.toByteArray();
    }

    /**
     * 检查是否已经存在APE的标签了,主要查两个地方
     * 一个是最后的字节,还有一个是最后128字节以上的字节
     * 因为最后的字节可能写入了ID3v1标签
     * @param raf 文件
     * @return 得到标签头
     * @throws java.io.IOException
     */
    private TagHead checkTag(RandomAccessFile raf) throws IOException {
        raf.seek((int) (raf.length() - 32));
        byte[] buffer = new byte[32];
        raf.read(buffer);
        TagHead header = new TagHead(buffer);
        if (header.isValid()) {
            header.setIndex(0);
            return header;
        } else {
            raf.seek((int) (raf.length() - 32 - 128));
            raf.read(buffer);
            header = new TagHead(buffer);
            if (header.isValid()) {
                header.setIndex(128);
                return header;
            } else {
                return null;
            }
        }
    }

    /**
     * 删除标签,如果存在ID3v1的话,就要先保存它然后删除后面部份
     * 把它写回来
     * @param raf 写出文件
     * @param hasID3v1 是否有ID3v1标签
     * @throws java.io.IOException
     */
    public void delete(RandomAccessFile raf, boolean hasID3v1) throws IOException {
        //如果有ID3标签,则先把它缓存起来,总共128个字节
        byte[] temp = null;
        int deleteLength = 0;
        if (hasID3v1) {
            temp = new byte[128];
            raf.seek(raf.length() - 128);
            raf.read(temp);
            deleteLength += 128;
        }
        TagHead header = checkTag(raf);
        //如果有标头,则说明有APE的标签,还要多删一些
        if (header != null) {
            log.log(Level.INFO, "原来存在APEv2标签,先删除之...");
            int length = header.getTagSize();
            if (header.hasHeader()) {//如果有标头的话,长度还要加32个字节
                length += 32;
            }
            deleteLength += length;
        }
        raf.setLength(raf.length() - deleteLength);
        log.log(Level.INFO, "APEv2标签删除完毕...");
    }

    public void add(TagField field) throws FieldDataInvalidException {
    }

    public void addAlbum(String album) throws FieldDataInvalidException {
        setAlbum(album);
    }

    public void addArtist(String artist) throws FieldDataInvalidException {
        setArtist(artist);
    }

    public void addComment(String comment) throws FieldDataInvalidException {
        setComment(comment);
    }

    public void addGenre(String genre) throws FieldDataInvalidException {
        setGenre(genre);
    }

    public void addTitle(String title) throws FieldDataInvalidException {
        setTitle(title);
    }

    public void addTrack(String track) throws FieldDataInvalidException {
        setTrack(track);
    }

    public void addYear(String year) throws FieldDataInvalidException {
        setYear(year);
    }

    public List<TagField> get(String id) {
        return null;
    }

    public List<TagField> getAlbum() {
        if (getFirstAlbum().length() > 0) {
            TagItem field = new TagItem(APEv2FieldKey.Album.name(), getFirstAlbum());
            return returnFieldToList(field);
        } else {
            return new ArrayList<TagField>();
        }
    }

    public List<TagField> getArtist() {
        if (getFirstAlbum().length() > 0) {
            TagItem field = new TagItem(APEv2FieldKey.Artist.name(), getFirstArtist());
            return returnFieldToList(field);
        } else {
            return new ArrayList<TagField>();
        }
    }

    public List<TagField> getComment() {
        if (getFirstAlbum().length() > 0) {
            TagItem field = new TagItem(APEv2FieldKey.Comment.name(), getFirstComment());
            return returnFieldToList(field);
        } else {
            return new ArrayList<TagField>();
        }
    }

    public List<TagField> getGenre() {
        if (getFirstAlbum().length() > 0) {
            TagItem field = new TagItem(APEv2FieldKey.Genre.name(), getFirstGenre());
            return returnFieldToList(field);
        } else {
            return new ArrayList<TagField>();
        }
    }

    public List<TagField> getTitle() {
        if (getFirstAlbum().length() > 0) {
            TagItem field = new TagItem(APEv2FieldKey.Title.name(), getFirstTitle());
            return returnFieldToList(field);
        } else {
            return new ArrayList<TagField>();
        }
    }

    public List<TagField> getTrack() {
        if (getFirstAlbum().length() > 0) {
            TagItem field = new TagItem(APEv2FieldKey.Track.name(), getFirstTrack());
            return returnFieldToList(field);
        } else {
            return new ArrayList<TagField>();
        }
    }

    public List<TagField> getYear() {
        if (getFirstAlbum().length() > 0) {
            TagItem field = new TagItem(APEv2FieldKey.Year.name(), getFirstYear());
            return returnFieldToList(field);
        } else {
            return new ArrayList<TagField>();
        }
    }

    public String getFirstAlbum() {
        return this.album;
    }

    public String getFirstArtist() {
        return artist;
    }

    public String getFirstComment() {
        return comment;
    }

    public String getFirstGenre() {
        return genre;
    }

    public String getFirstTitle() {
        return title;
    }

    public String getFirstTrack() {
        return track;
    }

    public String getFirstYear() {
        return year;
    }

    public boolean hasCommonFields() {
        return true;
    }

    public boolean hasField(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void set(TagField field) throws FieldDataInvalidException {
        TagFieldKey genericKey = TagFieldKey.valueOf(field.getId());
        switch (genericKey) {
            case ARTIST:
                setArtist(field.toString());
                break;
            case ALBUM:
                setAlbum(field.toString());
                break;
            case TITLE:
                setTitle(field.toString());
                break;
            case GENRE:
                setGenre(field.toString());
                break;
            case YEAR:
                setYear(field.toString());
                break;
            case COMMENT:
                setComment(field.toString());
                break;
		default:
			break;
        }
    }

    public void setAlbum(String s) throws FieldDataInvalidException {
        this.album = s;
        map.put(APEv2FieldKey.Album.name(), album);
    }

    public void setArtist(String s) throws FieldDataInvalidException {
        this.artist = s;
        map.put(APEv2FieldKey.Artist.name(), s);
    }

    public void setComment(String s) throws FieldDataInvalidException {
        this.comment = s;
        map.put(APEv2FieldKey.Comment.name(), s);
    }

    public void setGenre(String s) throws FieldDataInvalidException {
        this.genre = s;
        map.put(APEv2FieldKey.Genre.name(), s);
    }

    public void setTitle(String s) throws FieldDataInvalidException {
        this.title = s;
        map.put(APEv2FieldKey.Title.name(), s);
    }

    public void setTrack(String s) throws FieldDataInvalidException {
        this.track = s;
        map.put(APEv2FieldKey.Track.name(), s);
    }

    public void setYear(String s) throws FieldDataInvalidException {
        this.year = s;
        map.put(APEv2FieldKey.Year.name(), s);
    }

    public TagField createTagField(TagFieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getFirst(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getFirst(TagFieldKey id) throws KeyNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TagField getFirstField(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteTagField(TagFieldKey tagFieldKey) throws KeyNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<?> getFields() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getFieldCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean setEncoding(String enc) throws FieldDataInvalidException {
        return false;
    }

    public List<TagField> get(TagFieldKey id) throws KeyNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void main(String[] args) throws Exception {
        System.out.println(0xD2);
//        APEv2Tag tag = new APEv2Tag(new File("D:\\难道爱一个人有错吗.mp3"));
//        tag.load();
//        System.out.println("tag.album:" + tag.getFirstAlbum());
//        System.out.println("tag.title:" + tag.getFirstTitle());
//        System.out.println("tag.artist:" + tag.getFirstArtist());
    }
}
