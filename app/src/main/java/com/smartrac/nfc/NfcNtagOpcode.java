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


public class NfcNtagOpcode {
    public static final byte GET_VERSION = 0x60; // 96
    public static final byte READ = 0x30; // 48
    public static final byte FAST_READ = 0x3A; // 58
    public static final byte WRITE = (byte) 0xA2; // -94
    public static final byte READ_CNT = 0x39; // 57
    public static final byte PWD_AUTH = 0x1B; // 27
    public static final byte READ_SIG = 0x3C; // 60
    public static final byte SECTOR_SELECT = (byte) 0xC2; // -62
    public static final byte READ_TT_STATUS = (byte) 0xA4;
    public static final byte MFULC_AUTH1 = 0x1A;
    public static final byte MFULC_AUTH2 = (byte) 0xAF;

    // IMPORTED FROM DECODE
    public static final byte AMIIBO_ACTIVATE_BANK = -89;
    public static final byte AMIIBO_FAST_READ = 59;
    public static final byte AMIIBO_FAST_WRITE = -82;
    public static final byte AMIIBO_GET_VERSION = 85;
    public static final byte AMIIBO_LOCK = 70;
    public static final byte AMIIBO_READ_SIG = 67;
    public static final byte AMIIBO_SET_BANKCOUNT = -87;
    public static final byte AMIIBO_UNLOCK_1 = 68;
    public static final byte AMIIBO_UNLOCK_2 = 69;
    public static final byte AMIIBO_WRITE = -91;
}
