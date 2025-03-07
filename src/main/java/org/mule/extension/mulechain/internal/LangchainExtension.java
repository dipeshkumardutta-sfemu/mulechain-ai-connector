package org.mule.extension.mulechain.internal;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.extension.mulechain.internal.embedding.models.LangchainEmbeddingModelConfiguration;
import org.mule.extension.mulechain.internal.llm.LangchainLLMConfiguration;
import org.mule.extension.mulechain.internal.tools.LangchainToolsConfiguration;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;


/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "mulechain")
@Extension(name = "MuleChain AI")
@Configurations({LangchainLLMConfiguration.class})
public class LangchainExtension {

}
