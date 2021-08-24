package com.smartrac.nfc;

/*
 * *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*
 * SMARTRAC SDK for Android NFC NTAG
 * ===============================================================================
 * Copyright (C) 2016 SMARTRAC TECHNOLOGY GROUP
 * ===============================================================================
 * SMARTRAC SDK
 * (C) Copyright 2016, Smartrac Technology Fletcher, Inc.
 * 267 Cane Creek Rd, Fletcher, NC, 28732, USA
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
 */


public class NfcNtagVersion {

    public static NfcNtagVersion fromGetVersion(byte[] getVersionBytes) {
        NfcNtagVersion nfcNtagVersion = new NfcNtagVersion(getVersionBytes);
        return nfcNtagVersion;
    }

    public NfcNtagVersion(byte[] versionBytes) {
        sNtagType = null;
        iMemSize = 0;
        if (versionBytes != null)
        {
            if ((versionBytes[1] == VENDOR_NXP) && (versionBytes[2] == PROD_NTAG) &&
                    (versionBytes.length == 8)) {
                switch (versionBytes[3]) {
                    case SUBTYPE_NTAG:
                        switch (versionBytes[6]) {
                            case STORAGE_NTAG210:
                                iMemSize = SIZE_NTAG210;
                                sNtagType = TYPE_NTAG210;
                                break;
                            case STORAGE_NTAG212:
                                iMemSize = SIZE_NTAG212;
                                sNtagType = TYPE_NTAG212;
                                break;
                            case STORAGE_NTAG213:
                                iMemSize = SIZE_NTAG213;
                                sNtagType = versionBytes[4] == 3 ? TYPE_NTAG213TT : TYPE_NTAG213;
                                break;
                            case STORAGE_NTAG215:
                                iMemSize = SIZE_NTAG215;
                                sNtagType = TYPE_NTAG215;
                                break;
                            case STORAGE_NTAG216:
                                iMemSize = SIZE_NTAG216;
                                sNtagType = TYPE_NTAG216;
                                break;
                            default:
                                iMemSize = SIZE_UNKNOWN;
                                sNtagType = TYPE_UNKNOWN;
                                break;
                        }
                        break;
                    case SUBTYPE_NTAG_F:
                        switch (versionBytes[6]) {
                            case STORAGE_NTAG213:
                                iMemSize = SIZE_NTAG213;
                                sNtagType = TYPE_NTAG213F;
                                break;
                            case STORAGE_NTAG216:
                                iMemSize = SIZE_NTAG216;
                                sNtagType = TYPE_NTAG216F;
                                break;
                            default:
                                iMemSize = SIZE_UNKNOWN;
                                sNtagType = TYPE_UNKNOWN;
                                break;
                        }
                        break;
                    case SUBTYPE_NTAG_I2C:
                        switch (versionBytes[6]) {
                            case STORAGE_NTAG1K:
                                iMemSize = SIZE_NTAG1K;
                                if (versionBytes[5] > 1)
                                    sNtagType = TYPE_NTAGI2CP1K;
                                else
                                    sNtagType = TYPE_NTAGI2C1K;
                                break;
                            case STORAGE_NTAG2K:
                                iMemSize = SIZE_NTAG2K;
                                if (versionBytes[5] > 1)
                                    sNtagType = TYPE_NTAGI2CP2K;
                                else
                                    sNtagType = TYPE_NTAGI2C2K;
                                break;
                            default:
                                iMemSize = SIZE_UNKNOWN;
                                sNtagType = TYPE_UNKNOWN;
                                break;
                        }
                        break;
                    default:
                        iMemSize = SIZE_UNKNOWN;
                        sNtagType = TYPE_UNKNOWN;
                        break;
                }
            }
        }
    }

    public int getMemorySize() {
        return iMemSize;
    }

    @Override
    public String toString() {
        return sNtagType;
    }

    static final byte   VENDOR_NXP = 0x04;
    static final byte   PROD_NTAG = 0x04;
    static final byte   SUBTYPE_NTAG = 0x02;
    static final byte   SUBTYPE_NTAG_F = 0x04;
    static final byte   SUBTYPE_NTAG_I2C = 0x05;
    static final byte   STORAGE_NTAG210 = 0x0B;
    static final byte   STORAGE_NTAG212 = 0x0E;
    static final byte   STORAGE_NTAG213 = 0x0F;
    static final byte   STORAGE_NTAG215 = 0x11;
    static final byte   STORAGE_NTAG216 = 0x13;
    static final byte   STORAGE_NTAG1K = STORAGE_NTAG216;
    static final byte   STORAGE_NTAG2K = 0x15;
    static final int    SIZE_NTAG210 = 48;
    static final int    SIZE_NTAG212 = 128;
    static final int    SIZE_NTAG213 = 144;
    static final int    SIZE_NTAG215 = 504;
    static final int    SIZE_NTAG216 = 888;
    static final int    SIZE_NTAG1K = SIZE_NTAG216;
    static final int    SIZE_NTAG2K = 1904;
    static final int    SIZE_UNKNOWN = 0;
    static final String TYPE_NTAG210 = "NTAG 210";
    static final String TYPE_NTAG212 = "NTAG 212";
    static final String TYPE_NTAG213 = "NTAG 213";
    static final String TYPE_NTAG213TT = "NTAG 213 TT";
    static final String TYPE_NTAG215 = "NTAG 215";
    static final String TYPE_NTAG216 = "NTAG 216";
    static final String TYPE_NTAG213F = "NTAG 213F";
    static final String TYPE_NTAG216F = "NTAG 216F";
    static final String TYPE_NTAGI2C1K = "NTAG I2C 1K";
    static final String TYPE_NTAGI2C2K = "NTAG I2C 2K";
    static final String TYPE_NTAGI2CP1K = "NTAG I2C plus 1K";
    static final String TYPE_NTAGI2CP2K = "NTAG I2C plus 2K";
    static final String TYPE_UNKNOWN = "unknown chip";

    private int iMemSize;
    private String sNtagType;
}






/*
public class NfcNtagVersion_old {
    static final byte PROD_NTAG = 4;
    static final int SIZE_NTAG1K = 888;
    static final int SIZE_NTAG210 = 48;
    static final int SIZE_NTAG212 = 128;
    static final int SIZE_NTAG213 = 144;
    static final int SIZE_NTAG215 = 504;
    static final int SIZE_NTAG216 = 888;
    static final int SIZE_NTAG2K = 1904;
    static final int SIZE_UNKNOWN = 0;
    static final byte STORAGE_NTAG1K = 19;
    static final byte STORAGE_NTAG210 = 11;
    static final byte STORAGE_NTAG212 = 14;
    static final byte STORAGE_NTAG213 = 15;
    static final byte STORAGE_NTAG215 = 17;
    static final byte STORAGE_NTAG216 = 19;
    static final byte STORAGE_NTAG2K = 21;
    static final byte SUBTYPE_NTAG = 2;
    static final byte SUBTYPE_NTAG_F = 4;
    static final byte SUBTYPE_NTAG_I2C = 5;
    static final String TYPE_NTAG210 = "NTAG 210";
    static final String TYPE_NTAG212 = "NTAG 212";
    static final String TYPE_NTAG213 = "NTAG 213";
    static final String TYPE_NTAG213F = "NTAG 213F";
    static final String TYPE_NTAG215 = "NTAG 215";
    static final String TYPE_NTAG216 = "NTAG 216";
    static final String TYPE_NTAG216F = "NTAG 216F";
    static final String TYPE_NTAGI2C1K = "NTAG I2C 1K";
    static final String TYPE_NTAGI2C2K = "NTAG I2C 2K";
    static final String TYPE_NTAGI2CP1K = "NTAG I2C plus 1K";
    static final String TYPE_NTAGI2CP2K = "NTAG I2C plus 2K";
    static final String TYPE_UNKNOWN = "unknown chip";
    static final byte VENDOR_NXP = 4;
    private int iMemSize = 0;
    private String sNtagType = null;

    public static NfcNtagVersion fromGetVersion(byte[] bArr) {
        return new NfcNtagVersion(bArr);
    }

    public NfcNtagVersion_old(byte[] bArr) {
        if (bArr != null && bArr[1] == 4 && bArr[2] == 4 && bArr.length == 8) {
            byte b = bArr[3];
            if (b == 2) {
                byte b2 = bArr[6];
                if (b2 == 11) {
                    this.iMemSize = 48;
                    this.sNtagType = TYPE_NTAG210;
                } else if (b2 == 17) {
                    this.iMemSize = SIZE_NTAG215;
                    this.sNtagType = TYPE_NTAG215;
                } else if (b2 == 19) {
                    this.iMemSize = 888;
                    this.sNtagType = TYPE_NTAG216;
                } else if (b2 == 14) {
                    this.iMemSize = 128;
                    this.sNtagType = TYPE_NTAG212;
                } else if (b2 != 15) {
                    this.iMemSize = 0;
                    this.sNtagType = TYPE_UNKNOWN;
                } else {
                    this.iMemSize = SIZE_NTAG213;
                    this.sNtagType = TYPE_NTAG213;
                }
            } else if (b == 4) {
                byte b3 = bArr[6];
                if (b3 == 15) {
                    this.iMemSize = SIZE_NTAG213;
                    this.sNtagType = TYPE_NTAG213F;
                } else if (b3 != 19) {
                    this.iMemSize = 0;
                    this.sNtagType = TYPE_UNKNOWN;
                } else {
                    this.iMemSize = 888;
                    this.sNtagType = TYPE_NTAG216F;
                }
            } else if (b != 5) {
                this.iMemSize = 0;
                this.sNtagType = TYPE_UNKNOWN;
            } else {
                byte b4 = bArr[6];
                if (b4 == 19) {
                    this.iMemSize = 888;
                    if (bArr[5] > 1) {
                        this.sNtagType = TYPE_NTAGI2CP1K;
                    } else {
                        this.sNtagType = TYPE_NTAGI2C1K;
                    }
                } else if (b4 != 21) {
                    this.iMemSize = 0;
                    this.sNtagType = TYPE_UNKNOWN;
                } else {
                    this.iMemSize = SIZE_NTAG2K;
                    if (bArr[5] > 1) {
                        this.sNtagType = TYPE_NTAGI2CP2K;
                    } else {
                        this.sNtagType = TYPE_NTAGI2C2K;
                    }
                }
            }
        }
    }

    public int getMemorySize() {
        return this.iMemSize;
    }

    public String toString() {
        return this.sNtagType;
    }
}
*/