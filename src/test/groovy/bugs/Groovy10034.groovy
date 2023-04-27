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
package groovy.bugs

import org.codehaus.groovy.classgen.asm.AbstractBytecodeTestCase

final class Groovy10034 extends AbstractBytecodeTestCase {
    void testObjectArrayParam() {
        def result = compile method:'test', '''
            @groovy.transform.CompileStatic
            def test() {
                ["x"].toArray(new String[0])
            }
        '''
        int offset = result.indexOf('ANEWARRAY java/lang/String', result.indexOf('--BEGIN--'))
        assert result.hasStrictSequence(['ANEWARRAY java/lang/String','INVOKEVIRTUAL java/util/ArrayList.toArray'], offset)
        // there should be no 'INVOKEDYNAMIC cast' instruction here: ^
    }
}
