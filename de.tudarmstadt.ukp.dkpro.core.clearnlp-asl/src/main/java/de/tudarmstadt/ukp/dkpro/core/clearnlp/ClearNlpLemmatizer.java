/*******************************************************************************
 * Copyright 2012
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
package de.tudarmstadt.ukp.dkpro.core.clearnlp;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.uimafit.util.JCasUtil.select;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import com.googlecode.clearnlp.engine.EngineGetter;
import com.googlecode.clearnlp.morphology.AbstractMPAnalyzer;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * Lemmatizer using Clear NLP.
 * 
 * @author Richard Eckart de Castilho
 */
public class ClearNlpLemmatizer
	extends JCasAnnotator_ImplBase
{
	/**
	 * Use this language instead of the document language to resolve the model.
	 */
	public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
	protected String language;

	/**
	 * Override the default variant used to locate the model.
	 */
	public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
	@ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
	protected String variant;

	/**
	 * Load the model from this location instead of locating the model automatically.
	 */
	public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
	protected String modelLocation;

	private CasConfigurableProviderBase<AbstractMPAnalyzer> modelProvider;
	
	@Override
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);

		modelProvider = new CasConfigurableProviderBase<AbstractMPAnalyzer>()
		{
			{
				setDefault(VERSION, "20121017.0");
				setDefault(GROUP_ID, "de.tudarmstadt.ukp.dkpro.core");
				setDefault(ARTIFACT_ID,
						"de.tudarmstadt.ukp.dkpro.core.clearnlp-model-morph-${language}-${variant}");
				
				setDefault(LOCATION, "classpath:/de/tudarmstadt/ukp/dkpro/core/clearnlp/lib/" +
						"morph-${language}-${variant}.bin");
				setDefault(VARIANT, "default");
				
				setOverride(LOCATION, modelLocation);
				setOverride(LANGUAGE, language);
				setOverride(VARIANT, variant);
			}

			@Override
			protected AbstractMPAnalyzer produceResource(URL aUrl)
				throws IOException
			{
				InputStream is = null;
				try {
					is = aUrl.openStream();
					String language = getAggregatedProperties().getProperty(PARAM_LANGUAGE);
					return EngineGetter.getMPAnalyzer(language, is);
				}
				catch (Exception e) {
					throw new IOException(e);
				}
				finally {
					closeQuietly(is);
				}
			}
		};
	}

	@Override
	public void process(JCas aJCas)
		throws AnalysisEngineProcessException
	{
		modelProvider.configure(aJCas.getCas());
		AbstractMPAnalyzer lemmatizer = modelProvider.getResource();
		
		
		for (Token t : select(aJCas, Token.class)) {
			Lemma l = new Lemma(aJCas, t.getBegin(), t.getEnd());
			l.setValue(lemmatizer.getLemma(t.getCoveredText(), t.getPos().getPosValue()));
			l.addToIndexes();
			
			t.setLemma(l);
		}
	}
}