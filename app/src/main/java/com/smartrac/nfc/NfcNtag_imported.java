//package com.smartrac.nfc;
//
//import android.nfc.Tag;
//import android.nfc.tech.NfcA;
//import android.nfc.tech.TagTechnology;
//import java.io.IOException;
//
//public class NfcNtag_old implements TagTechnology {
//    private int maxTranscieveLength = ((this.nfca.getMaxTransceiveLength() / 4) + 1);
//    /* access modifiers changed from: private */
//    public NfcA nfca;
//
//    private interface IFastRead {
//        byte[] doFastRead(int i, int i2, int i3);
//    }
//
//    private interface IFastWrite {
//        boolean doFastWrite(int i, int i2, byte[] bArr);
//    }
//
//    public static NfcNtag get(Tag tag) {
//        return new NfcNtag(tag);
//    }
//
//    public NfcNtag_old(Tag tag) {
//        this.nfca = NfcA.get(tag);
//    }
//
//    public void connect() throws IOException {
//        this.nfca.connect();
//    }
//
//    public void close() throws IOException {
//        this.nfca.close();
//    }
//
//    public int getMaxTransceiveLength() {
//        return this.maxTranscieveLength;
//    }
//
//    public Tag getTag() {
//        return this.nfca.getTag();
//    }
//
//    public boolean isConnected() {
//        return this.nfca.isConnected();
//    }
//
//    public byte[] transceive(byte[] bArr) {
//        try {
//            return this.nfca.transceive(bArr);
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public byte[] getVersion() {
//        try {
//            return this.nfca.transceive(new byte[]{NfcNtagOpcode.GET_VERSION});
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public byte[] amiiboGetVersion() {
//        try {
//            return this.nfca.transceive(new byte[]{NfcNtagOpcode.AMIIBO_GET_VERSION});
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public byte[] amiiboInitApdu() {
//        try {
//            return this.nfca.transceive(new byte[]{-12, 73, -101, -103, -61, -38, 87, 113, 10, 100, 74, -98, -8, NfcNtagOpcode.WRITE, NfcNtagOpcode.READ, -39});
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public byte[] read(int i) {
//        try {
//            return this.nfca.transceive(new byte[]{NfcNtagOpcode.READ, (byte) (i & 255)});
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public byte[] fastRead(int i, int i2) {
//        return internalFastRead(new IFastRead() {
//            public byte[] doFastRead(int i, int i2, int i3) {
//                try {
//                    return NfcNtag.this.nfca.transceive(new byte[]{NfcNtagOpcode.FAST_READ, (byte) (i & 255), (byte) (i2 & 255)});
//                } catch (Exception unused) {
//                    return null;
//                }
//            }
//        }, i, i2, 0);
//    }
//
//    public byte[] amiiboFastRead(int i, int i2, int appWriteBank) {
//        return internalFastRead(new IFastRead() {
//            public byte[] doFastRead(int i, int i2, int i3) {
//                try {
//                    return NfcNtag.this.nfca.transceive(new byte[]{NfcNtagOpcode.AMIIBO_FAST_READ, (byte) (i & 255), (byte) (i2 & 255), (byte) (i3 & 255)});
//                } catch (Exception unused) {
//                    return null;
//                }
//            }
//        }, i, i2, appWriteBank);
//    }
//
//    private byte[] internalFastRead(IFastRead iFastRead, int startAddr, int endAddr, int appWriteBank) {
//        if (endAddr < startAddr) {
//            return null;
//        }
//        byte[] bArr = new byte[(((endAddr - startAddr) + 1) * 4)];
//        int maxReadLenght = (this.maxTranscieveLength / 4) - 1;
//        if (maxReadLenght < 1) {
//            return null;
//        }
//        int i5 = maxReadLenght * 4;
//        int i = 0;
//        while (startAddr <= endAddr) {
//            int startSnippet = startAddr + maxReadLenght;
//            int endSnippet = startSnippet - 1;
//            if (endSnippet > endAddr) {
//                endSnippet = endAddr;
//            }
//            byte[] doFastRead = iFastRead.doFastRead(startAddr, endSnippet, appWriteBank);
//            if (doFastRead == null || doFastRead.length != ((endSnippet - startAddr) + 1) * 4) {
//                return null;
//            }
//            if (doFastRead.length == bArr.length) {
//                return doFastRead;
//            }
//            System.arraycopy(doFastRead, 0, bArr, i * i5, doFastRead.length);
//            i++;
//            startAddr = startSnippet;
//        }
//        return bArr;
//    }
//
//    public boolean write(int i, byte[] bArr) {
//        if (bArr == null || bArr.length != 4) {
//            return false;
//        }
//        byte[] bArr2 = new byte[6];
//        bArr2[0] = NfcNtagOpcode.WRITE;
//        bArr2[1] = (byte) (i & 255);
//        try {
//            System.arraycopy(bArr, 0, bArr2, 2, 4);
//            this.nfca.transceive(bArr2);
//            return true;
//        } catch (Exception unused) {
//            return false;
//        }
//    }
//
//    private boolean internalWrite(IFastWrite iFastWrite, int i, int i2, byte[] bArr) {
//        byte[] bArr2 = new byte[4];
//        int length = bArr.length / 4;
//        for (int i3 = 0; i3 < length; i3++) {
//            System.arraycopy(bArr, i3 * 4, bArr2, 0, 4);
//            if (!iFastWrite.doFastWrite(i + i3, i2, bArr2)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public boolean amiiboWrite(int i, int i2, byte[] bArr) {
//        if (bArr != null && bArr.length % 4 == 0) {
//            return internalWrite(new IFastWrite() {
//                public boolean doFastWrite(int i, int i2, byte[] bArr) {
//                    byte[] bArr2 = new byte[7];
//                    bArr2[0] = NfcNtagOpcode.AMIIBO_WRITE;
//                    bArr2[1] = (byte) (i & 255);
//                    bArr2[2] = (byte) (i2 & 255);
//                    try {
//                        System.arraycopy(bArr, 0, bArr2, 3, 4);
//                        NfcNtag.this.nfca.transceive(bArr2);
//                        return true;
//                    } catch (Exception unused) {
//                        return false;
//                    }
//                }
//            }, i, i2, bArr);
//        }
//        return false;
//    }
//
//    private boolean internalFastWrite(IFastWrite iFastWrite, int i, int i2, byte[] bArr) {
//        int length = (bArr.length / 4) + i;
//        int i3 = 16;
//        int i4 = 0;
//        while (i <= length) {
//            int i5 = i + 4;
//            if (i5 >= length) {
//                i3 = bArr.length % i3;
//            }
//            if (i3 == 0) {
//                return true;
//            }
//            byte[] bArr2 = new byte[i3];
//            System.arraycopy(bArr, i4, bArr2, 0, i3);
//            if (!iFastWrite.doFastWrite(i, i2, bArr2)) {
//                return false;
//            }
//            i4 += i3;
//            i = i5;
//        }
//        return true;
//    }
//
//    public boolean amiiboFastWrite(int i, int i2, byte[] bArr) {
//        if (bArr == null) {
//            return false;
//        }
//        return internalFastWrite(new IFastWrite() {
//            public boolean doFastWrite(int i, int i2, byte[] bArr) {
//                byte[] bArr2 = new byte[(bArr.length + 4)];
//                bArr2[0] = NfcNtagOpcode.AMIIBO_FAST_WRITE;
//                bArr2[1] = (byte) (i & 255);
//                bArr2[2] = (byte) (i2 & 255);
//                bArr2[3] = (byte) (bArr.length & 255);
//                try {
//                    System.arraycopy(bArr, 0, bArr2, 4, bArr.length);
//                    NfcNtag.this.nfca.transceive(bArr2);
//                    return true;
//                } catch (Exception unused) {
//                    return false;
//                }
//            }
//        }, i, i2, bArr);
//    }
//
//    public int readCnt() {
//        try {
//            byte[] transceive = this.nfca.transceive(new byte[]{NfcNtagOpcode.READ_CNT, 2});
//            return transceive[0] + (transceive[1] * NfcNtagConst.NAK_INVALID_ARGUMENT) + (transceive[2] * NfcNtagConst.NAK_INVALID_ARGUMENT);
//        } catch (Exception unused) {
//            return -1;
//        }
//    }
//
//    public byte[] pwdAuth(byte[] bArr) {
//        if (bArr == null || bArr.length != 4) {
//            return null;
//        }
//        byte[] bArr2 = new byte[5];
//        bArr2[0] = NfcNtagOpcode.PWD_AUTH;
//        try {
//            System.arraycopy(bArr, 0, bArr2, 1, 4);
//            return this.nfca.transceive(bArr2);
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public byte[] readSig() {
//        try {
//            return this.nfca.transceive(new byte[]{NfcNtagOpcode.READ_SIG, 0});
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public byte[] amiiboReadSig() {
//        try {
//            return this.nfca.transceive(new byte[]{NfcNtagOpcode.AMIIBO_READ_SIG});
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public byte[] amiiboLock() {
//        try {
//            return this.nfca.transceive(new byte[]{NfcNtagOpcode.AMIIBO_LOCK});
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public byte[] amiiboPrepareUnlock() {
//        try {
//            return this.nfca.transceive(new byte[]{NfcNtagOpcode.AMIIBO_UNLOCK_1});
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public byte[] amiiboUnlock() {
//        try {
//            return this.nfca.transceive(new byte[]{NfcNtagOpcode.AMIIBO_UNLOCK_2});
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public boolean sectorSelect(byte b) {
//        byte[] bArr = new byte[4];
//        try {
//            this.nfca.transceive(new byte[]{NfcNtagOpcode.SECTOR_SELECT, -1});
//            bArr[0] = b;
//            bArr[1] = 0;
//            try {
//                this.nfca.transceive(bArr);
//                return false;
//            } catch (Exception unused) {
//                return true;
//            }
//        } catch (Exception unused2) {
//            return false;
//        }
//    }
//
//    public byte[] amiiboSetBankcount(int i) {
//        try {
//            return this.nfca.transceive(new byte[]{NfcNtagOpcode.AMIIBO_SET_BANKCOUNT, (byte) (i & 255)});
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//
//    public byte[] amiiboActivateBank(int i) {
//        try {
//            return this.nfca.transceive(new byte[]{NfcNtagOpcode.AMIIBO_ACTIVATE_BANK, (byte) (i & 255)});
//        } catch (Exception unused) {
//            return null;
//        }
//    }
//}
