package com.xpg.convertor;

import com.xpg.constant.Constants;
import com.xpg.util.ByteUtil;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProtocolConvertor {
    public int byteTotal;
    protected Map<String, ByteDescriptor> descriptors = new HashMap();
    public String header = Constants.CURRENT_MODEL;
    protected Map<String, ByteDescriptor> receiveDescriptors = new HashMap();
    public int reverse;

    class ByteDescriptor {
        public int begin;
        public String byteKey;
        public int end;
        public int index;
        public int max;
        public int mid;
        public int min;
        public int shift;

        ByteDescriptor() {
        }

        public byte[] generate(byte[] bytes) {
            return bytes;
        }

        public String toString() {
            return this.byteKey + ":" + this.index + ":" + this.max + ":" + this.mid + ":" + this.min + ":" + this.shift + ":" + this.begin + ":" + this.end;
        }
    }

    public ProtocolConvertor initConfig(InputStream is, String modelKey) {
        return initProtocolProcess(is, "desc", modelKey);
    }

    public ProtocolConvertor initConfig(InputStream is, String matchKey, String modelKey) {
        return initProtocolProcess(is, matchKey, modelKey);
    }

    public ProtocolConvertor initProtocolProcess(InputStream is, String matchKey, String modelKey) {
        try {
            Element model;
            Iterator<?> iterator;
            Element e;
            ByteDescriptor bd;
            Element root = new SAXReader().read(is).getRootElement();
            Iterator<?> rootIterator = root.elementIterator();
            while (rootIterator.hasNext()) {
                model = ((Element) rootIterator.next()).element(ProtocolConstant.MODEL);
                if (model.elementText(matchKey).equals(modelKey)) {
                    Element protocol = model.element(ProtocolConstant.PROTOCOl);
                    this.byteTotal = Integer.parseInt(protocol.element(ProtocolConstant.BYTE_TOTAL).getData().toString());
                    this.reverse = Integer.parseInt(protocol.element(ProtocolConstant.REVERSE).getData().toString());
                    this.header = protocol.element(ProtocolConstant.HEADER).getData().toString();
                    iterator = protocol.element(ProtocolConstant.BYTES).elementIterator();
                    while (iterator.hasNext()) {
                        e = (Element) iterator.next();
                        bd = new ByteDescriptor();
                        bd.byteKey = e.elementText("desc");
                        bd.index = Integer.parseInt(e.elementText(ProtocolConstant.BYTE_INDEX));
                        bd.max = Integer.parseInt(e.elementText(ProtocolConstant.MAX_VALUE));
                        bd.mid = Integer.parseInt(e.elementText(ProtocolConstant.MID_VALUE));
                        bd.min = Integer.parseInt(e.elementText(ProtocolConstant.MIN_VALUE));
                        bd.shift = Integer.parseInt(e.elementText(ProtocolConstant.BYTE_SHIFT));
                        bd.begin = Integer.parseInt(e.elementText(ProtocolConstant.BEGIN_BIT));
                        bd.end = Integer.parseInt(e.elementText(ProtocolConstant.END_BIT));
                        this.descriptors.put(bd.byteKey, bd);
                    }
                    rootIterator = root.elementIterator();
                    while (rootIterator.hasNext()) {
                        model = ((Element) rootIterator.next()).element(ProtocolConstant.MODEL);
                        if (model.elementText(matchKey).equals(modelKey)) {
                            iterator = model.element(ProtocolConstant.RECEIVE).element(ProtocolConstant.BYTES).elementIterator();
                            while (iterator.hasNext()) {
                                e = (Element) iterator.next();
                                bd = new ByteDescriptor();
                                bd.byteKey = e.elementText("desc");
                                bd.index = Integer.parseInt(e.elementText(ProtocolConstant.BYTE_INDEX));
                                bd.max = Integer.parseInt(e.elementText(ProtocolConstant.MAX_VALUE));
                                bd.mid = Integer.parseInt(e.elementText(ProtocolConstant.MID_VALUE));
                                bd.min = Integer.parseInt(e.elementText(ProtocolConstant.MIN_VALUE));
                                bd.shift = Integer.parseInt(e.elementText(ProtocolConstant.BYTE_SHIFT));
                                bd.begin = Integer.parseInt(e.elementText(ProtocolConstant.BEGIN_BIT));
                                bd.end = Integer.parseInt(e.elementText(ProtocolConstant.END_BIT));
                                this.receiveDescriptors.put(bd.byteKey, bd);
                            }
                            return this;
                        }
                    }
                    return this;
                }
            }
            rootIterator = root.elementIterator();
            while (rootIterator.hasNext()) {
                model = ((Element) rootIterator.next()).element(ProtocolConstant.MODEL);
                if (model.elementText(matchKey).equals(modelKey)) {
                    iterator = model.element(ProtocolConstant.RECEIVE).element(ProtocolConstant.BYTES).elementIterator();
                    while (iterator.hasNext()) {
                        e = (Element) iterator.next();
                        bd = new ByteDescriptor();
                        bd.byteKey = e.elementText("desc");
                        bd.index = Integer.parseInt(e.elementText(ProtocolConstant.BYTE_INDEX));
                        bd.max = Integer.parseInt(e.elementText(ProtocolConstant.MAX_VALUE));
                        bd.mid = Integer.parseInt(e.elementText(ProtocolConstant.MID_VALUE));
                        bd.min = Integer.parseInt(e.elementText(ProtocolConstant.MIN_VALUE));
                        bd.shift = Integer.parseInt(e.elementText(ProtocolConstant.BYTE_SHIFT));
                        bd.begin = Integer.parseInt(e.elementText(ProtocolConstant.BEGIN_BIT));
                        bd.end = Integer.parseInt(e.elementText(ProtocolConstant.END_BIT));
                        this.receiveDescriptors.put(bd.byteKey, bd);
                    }
                    return this;
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return this;
    }

    public byte[] convertToBytes(String key, float value, byte[] sendBytes, boolean convert) {
        if (sendBytes == null) {
            sendBytes = new byte[this.byteTotal];
        }
        ByteDescriptor bd = (ByteDescriptor) this.descriptors.get(key);
        if (convert) {
            value = (float) ((int) ((((double) value) / 100.0d) * ((double) (bd.max - bd.min))));
        }
        if (value < ((float) bd.min)) {
            value = (float) bd.min;
        }
        if (value > ((float) bd.max)) {
            value = (float) bd.max;
        }
        int i = bd.index;
        sendBytes[i] = (byte) (sendBytes[i] + ((byte) (((byte) ((int) value)) << bd.shift)));
        return sendBytes;
    }

    public byte[] convertToBytes(String key, float value, byte[] sendBytes, boolean convert, boolean negative) {
        if (sendBytes == null) {
            sendBytes = new byte[this.byteTotal];
        }
        ByteDescriptor bd = (ByteDescriptor) this.descriptors.get(key);
        if (convert) {
            if (!negative) {
                value = (float) ((int) ((((double) value) / 100.0d) * ((double) (bd.max - bd.min))));
            } else if (value > 50.0f) {
                value = ((value - 50.0f) / 50.0f) * ((float) (bd.max - bd.mid));
            } else if (value < 50.0f) {
                value = ((-(50.0f - value)) / 50.0f) * ((float) (bd.mid - bd.min));
            } else {
                value = (float) bd.mid;
            }
        }
        if (value < ((float) bd.min)) {
            value = (float) bd.min;
        }
        if (value > ((float) bd.max)) {
            value = (float) bd.max;
        }
        int i = bd.index;
        sendBytes[i] = (byte) (sendBytes[i] + ((byte) (((byte) ((int) value)) << bd.shift)));
        return sendBytes;
    }

    public int[] convertToInts(String key, float value, int[] sendInts, boolean convert) {
        if (sendInts == null) {
            sendInts = new int[this.byteTotal];
        }
        ByteDescriptor bd = (ByteDescriptor) this.descriptors.get(key);
        if (value < ((float) bd.min)) {
            value = (float) bd.min;
        }
        if (value > ((float) bd.max)) {
            value = (float) bd.max;
        }
        int i = bd.index;
        sendInts[i] = sendInts[i] + (((int) value) << bd.shift);
        return sendInts;
    }

    public String intsToString(int[] ints) {
        return ByteUtil.Ints2HexString(ints);
    }

    public byte[] stringToBytes(String str) {
        byte[] sendBuf = new byte[str.length()];
        for (int i = 0; i < str.length(); i++) {
            sendBuf[i] = (byte) str.charAt(i);
        }
        return sendBuf;
    }

    public Map<String, Float> convertReceiveData(int data, int index, Map<String, Float> map) {
        String dataString = ByteUtil.toBinaryString(data);
        Iterator<?> it = this.receiveDescriptors.keySet().iterator();
        while (it.hasNext()) {
            ByteDescriptor bd = (ByteDescriptor) this.receiveDescriptors.get((String) it.next());
            if (bd.index == index) {
                int byteInt = ByteUtil.toTenFromBinaryString(dataString.substring(bd.begin, bd.end + 1));
                map.put(bd.byteKey, Float.valueOf((float) byteInt));
                data = (data - byteInt) << bd.shift;
            }
        }
        return map;
    }
}
