/*******************************************************************************
 * Copyright 2011
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tudarmstadt.ukp.dkpro.core.toolbox.corpus;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.toolbox.core.Sentence;

public class TazCorpusTest
{

    @Ignore
    @Test
    public void tazTest() throws Exception {
        TazCorpus corpus = new TazCorpus();
        for (Sentence s : corpus.getSentences()) {
            for (int i=0; i<s.getTokens().size(); i++) {
                System.out.print(s.getTokens().get(i));
            }
        }
    }
    
}