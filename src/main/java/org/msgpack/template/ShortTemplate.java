//
// MessagePack for Java
//
// Copyright (C) 2009-2011 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.template;

import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.MessageTypeException;


public class ShortTemplate implements Template<Short> {
    private ShortTemplate() { }

    public void write(Packer pk, Short target) throws IOException {
        if(target == null) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeShort(target);
    }

    public Short read(Unpacker u, Short to) throws IOException {
        return u.readShort();
    }

    static public ShortTemplate getInstance() {
        return instance;
    }

    static final ShortTemplate instance = new ShortTemplate();
}
