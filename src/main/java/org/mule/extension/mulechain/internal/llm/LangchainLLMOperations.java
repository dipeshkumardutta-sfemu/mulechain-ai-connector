package org.mule.extension.mulechain.internal.llm;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.bedrock.BedrockAnthropicMessageChatModel;
import dev.langchain4j.model.bedrock.BedrockTitanChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;


import static java.time.Duration.ofSeconds;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import software.amazon.awssdk.regions.Region;
/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class LangchainLLMOperations {


	private static JSONObject readConfigFile(String filePath) {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            try {
                String content = new String(Files.readAllBytes(path));
                return new JSONObject(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //System.out.println("File does not exist: " + filePath);
        }
        return null;
    }

	private static OpenAiChatModel createOpenAiChatModel(String apiKey, LangchainLLMParameters LangchainParams) {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(LangchainParams.getModelName())
                .temperature(0.3)
                .timeout(ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();

    }

	private static MistralAiChatModel createMistralAiChatModel(String apiKey, LangchainLLMParameters LangchainParams) {
        return MistralAiChatModel.builder()
				//.apiKey(configuration.getLlmApiKey())
				.apiKey(apiKey)
				.modelName(LangchainParams.getModelName())
				.temperature(0.3)
				.timeout(ofSeconds(60))
				.logRequests(true)
				.logResponses(true)
				.build();
    }

	private static OllamaChatModel createOllamaChatModel(String baseURL, LangchainLLMParameters LangchainParams) {
        return OllamaChatModel.builder()
				//.baseUrl(configuration.getLlmApiKey())
				.baseUrl(baseURL)
				.modelName(LangchainParams.getModelName())
				.build();
    }


	private static AnthropicChatModel createAnthropicChatModel(String apiKey, LangchainLLMParameters LangchainParams) {
        return AnthropicChatModel.builder()
				//.apiKey(configuration.getLlmApiKey())
				.apiKey(apiKey)
				.modelName(LangchainParams.getModelName())
				.logRequests(true)
				.logResponses(true)
				.build();
    }


	private static AzureOpenAiChatModel createAzureOpenAiChatModel(String apiKey, String llmEndpoint, String deploymentName, LangchainLLMParameters LangchainParams) {
        return AzureOpenAiChatModel.builder()
				.apiKey(apiKey)
				.endpoint(llmEndpoint)
				.deploymentName(deploymentName)
				.temperature(0.3)
				.logRequestsAndResponses(true)
				.build();
	}

	private static BedrockAnthropicMessageChatModel createAWSBedrockAnthropicChatModel(LangchainLLMParameters LangchainParams) {
        return BedrockAnthropicMessageChatModel.builder()
		.region(Region.US_EAST_1)
		.temperature(0.30f)
		.maxTokens(300)
		.model(LangchainParams.getModelName())
		.maxRetries(1)
		.build();

	}

	private static BedrockTitanChatModel createAWSBedrockTitanChatModel(LangchainLLMParameters LangchainParams) {
        return BedrockTitanChatModel
        .builder()
        .temperature(0.50f)
        .maxTokens(300)
        .region(Region.US_EAST_1)
        //.model(BedrockAnthropicMessageChatModel.Types.AnthropicClaude3SonnetV1.getValue())
        .model(LangchainParams.getModelName())
        .maxRetries(1)
        // Other parameters can be set as well
        .build();

	}



	private ChatLanguageModel createModel(LangchainLLMConfiguration configuration, LangchainLLMParameters LangchainParams) {
	    ChatLanguageModel model = null;
        JSONObject config = readConfigFile(configuration.getFilePath());

	    switch (configuration.getLlmType()) {
	        case "OPENAI":
				if (configuration.getConfigType() .equals("Environment Variables")) {
					model = createOpenAiChatModel(System.getenv("OPENAI_API_KEY").replace("\n", "").replace("\r", ""), LangchainParams);
				} else {
					JSONObject llmType = config.getJSONObject("OPENAI");
					String llmTypeKey = llmType.getString("OPENAI_API_KEY");
					model = createOpenAiChatModel(llmTypeKey, LangchainParams);

				}
	            break;
	        case "MISTRAL_AI":
				if (configuration.getConfigType() .equals("Environment Variables")) {
					model = createMistralAiChatModel(System.getenv("MISTRAL_AI_API_KEY").replace("\n", "").replace("\r", ""), LangchainParams);
				} else {
					JSONObject llmType = config.getJSONObject("MISTRAL_AI");
					String llmTypeKey = llmType.getString("MISTRAL_AI_API_KEY");
					model = createMistralAiChatModel(llmTypeKey, LangchainParams);

				}
	            break;
	        case "OLLAMA":
				if (configuration.getConfigType() .equals("Environment Variables")) {
					model = createOllamaChatModel(System.getenv("OLLAMA_BASE_URL").replace("\n", "").replace("\r", ""), LangchainParams);
				} else {
					JSONObject llmType = config.getJSONObject("OLLAMA");
					String llmTypeUrl = llmType.getString("OLLAMA_BASE_URL");
					model = createOllamaChatModel(llmTypeUrl, LangchainParams);

				}
	            break;
	        case "ANTHROPIC":
				if (configuration.getConfigType() .equals("Environment Variables")) {
					model = createAnthropicChatModel(System.getenv("ANTHROPIC_API_KEY").replace("\n", "").replace("\r", ""), LangchainParams);
				} else {
					JSONObject llmType = config.getJSONObject("ANTHROPIC");
					String llmTypeKey = llmType.getString("ANTHROPIC_API_KEY");
					model = createAnthropicChatModel(llmTypeKey, LangchainParams);
				}
	            break;
			case "AWS_BEDROCK":
				//String[] creds = configuration.getLlmApiKey().split("mulechain"); 
				// For authentication, set the following environment variables:
        		// AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY
 				// model = BedrockAnthropicMessageChatModel.builder()
				// 		.region(Region.US_EAST_1)
				// 		.temperature(0.30f)
				// 		.maxTokens(300)
				// 		.model(LangchainParams.getModelName())
				// 		.maxRetries(1)
				// 		.build();
				//model = createAWSBedrockAnthropicChatModel(LangchainParams);
				model = createAWSBedrockTitanChatModel(LangchainParams);

				break;
			case "AZURE_OPENAI":
 				if (configuration.getConfigType() .equals("Environment Variables")) {
					model = createAzureOpenAiChatModel(System.getenv("AZURE_OPENAI_KEY").replace("\n", "").replace("\r", ""), 
														System.getenv("AZURE_OPENAI_ENDPOINT").replace("\n", "").replace("\r", ""), 
														System.getenv("AZURE_OPENAI_DEPLOYMENT_NAME").replace("\n", "").replace("\r", ""), LangchainParams);
				} else {
					JSONObject llmType = config.getJSONObject("AZURE_OPENAI");
					String llmTypeKey = llmType.getString("AZURE_OPENAI_KEY");
					String llmEndpoint = llmType.getString("AZURE_OPENAI_ENDPOINT");
					String llmDeploymentName = llmType.getString("AZURE_OPENAI_DEPLOYMENT_NAME");
					model = createAzureOpenAiChatModel(llmTypeKey, llmEndpoint, llmDeploymentName, LangchainParams);
				}
				break;
	        default:
	            throw new IllegalArgumentException("Unsupported LLM type: " + configuration.getLlmType());
	    }
	    return model;
	}

  
  /**
   * Implements a simple Chat agent
   */
  @MediaType(value = ANY, strict = false)
  @Alias("CHAT-answer-prompt")  
  public String answerPromptByModelName(String prompt, @Config LangchainLLMConfiguration configuration, @ParameterGroup(name= "Additional properties") LangchainLLMParameters LangchainParams) {
      // OpenAI parameters are explained here: https://platform.openai.com/docs/api-reference/chat/create

	  
	    ChatLanguageModel model = createModel(configuration, LangchainParams);



	    String response = model.generate(prompt);

	    // System.out.println(response);
	    return response;

 }
  
  
  
  /**
   * Helps defining an AI Agent with a prompt template
   */
  @MediaType(value = ANY, strict = false)
  @Alias("AGENT-define-prompt-template")  
  public String definePromptTemplate(String template, String instructions, String dataset, @Config LangchainLLMConfiguration configuration, @ParameterGroup(name= "Additional properties") LangchainLLMParameters LangchainParams) {

	      ChatLanguageModel model = createModel(configuration, LangchainParams);
	  

          String templateString = template;
          PromptTemplate promptTemplate = PromptTemplate.from(templateString + System.lineSeparator() + "Instructions: {{instructions}}" + System.lineSeparator() + "Dataset: {{dataset}}");

          Map<String, Object> variables = new HashMap<>();
          variables.put("instructions", instructions);
          variables.put("dataset", dataset);

          Prompt prompt = promptTemplate.apply(variables);

          String response = model.generate(prompt.text());

          //System.out.println(response);
      	return response;
      }

  
  
  
  /**
   * Supporting ENUM and Interface for Sentimetns
   */
  
  enum Sentiment {
      POSITIVE, NEUTRAL, NEGATIVE;
  }

  
  interface SentimentAnalyzer {

      @UserMessage("Analyze sentiment of {{it}}")
      Sentiment analyzeSentimentOf(String text);

      @UserMessage("Does {{it}} have a positive sentiment?")
      boolean isPositive(String text);
  }


  
  /**
   * Example of a sentiment analyzer, which accepts text as input.
   */
  @MediaType(value = ANY, strict = false)
  @Alias("SENTIMENT-analyze")  
  public Sentiment extractSentiments(String data, @Config LangchainLLMConfiguration configuration, @ParameterGroup(name= "Additional properties") LangchainLLMParameters LangchainParams) {
  
      ChatLanguageModel model = createModel(configuration, LangchainParams);
	  

      SentimentAnalyzer sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, model);

      Sentiment sentiment = sentimentAnalyzer.analyzeSentimentOf(data);
      System.out.println(sentiment); // POSITIVE

      boolean positive = sentimentAnalyzer.isPositive(data);
      System.out.println(positive); // false
      
      return sentiment;
  }

  
  
}
