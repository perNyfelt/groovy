/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.groovy.runtime.dgmimpl.arrays;

import groovy.lang.GString;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.PojoMetaMethodSite;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ShortArrayPutAtMetaMethod extends ArrayPutAtMetaMethod {
    private static final CachedClass ARRAY_CLASS = ReflectionCache.getCachedClass(short[].class);

    @Override
    public final CachedClass getDeclaringClass() {
        return ARRAY_CLASS;
    }

    @Override
    public Object invoke(Object object, Object[] args) {
        final short[] objects = (short[]) object;
        final int index = normaliseIndex((Integer) args[0], objects.length);
        Object newValue = args[1];
        if (!(newValue instanceof Short)) {
            if (newValue instanceof Character || newValue instanceof String || newValue instanceof GString) {
                Character ch = ShortTypeHandling.castToChar(newValue);
                objects[index] = (Short) DefaultTypeTransformation.castToType(ch, Short.class);
            } else {
                objects[index] = ((Number) newValue).shortValue();
            }
        } else
            objects[index] = (Short) args[1];
        return null;
    }

    @Override
    public CallSite createPojoCallSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params, Object receiver, Object[] args) {
        if (!(args[0] instanceof Integer) || !(args[1] instanceof Short))
            return PojoMetaMethodSite.createNonAwareCallSite(site, metaClass, metaMethod, params, args);
        else
            return new MyPojoMetaMethodSite(site, metaClass, metaMethod, params);
    }

    private static class MyPojoMetaMethodSite extends PojoMetaMethodSite {
        public MyPojoMetaMethodSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params) {
            super(site, metaClass, metaMethod, params);
        }

        @Override
        public Object call(Object receiver, Object[] args) throws Throwable {
            if ((receiver instanceof short[] && args[0] instanceof Integer && args[1] instanceof Short)
                    && checkPojoMetaClass()) {
                final short[] objects = (short[]) receiver;
                objects[normaliseIndex((Integer) args[0], objects.length)] = (Short) args[1];
                return null;
            } else
                return super.call(receiver, args);
        }

        @Override
        public Object call(Object receiver, Object arg1, Object arg2) throws Throwable {
            if (checkPojoMetaClass()) {
                try {
                    final short[] objects = (short[]) receiver;
                    objects[normaliseIndex((Integer) arg1, objects.length)] = (Short) arg2;
                    return null;
                }
                catch (ClassCastException e) {
                    if ((receiver instanceof short[]) && (arg1 instanceof Integer))
                        throw e;
                }
            }
            return super.call(receiver, arg1, arg2);
        }
    }
}
