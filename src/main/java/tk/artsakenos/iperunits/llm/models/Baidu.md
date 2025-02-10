

# Baidu

Link Utili
* [Sintesi Docs](https://docs.llamaindex.ai/en/stable/examples/llm/qianfan/)
* [Baidu AI Console](https://console.bce.baidu.com/qianfan/ais/console/onlineService)
* [Baidu Documentation](https://ai.baidu.com/ai-doc/REFERENCE/Ck3dwjgn3)

1. Visita l'[online service](https://console.bce.baidu.com/qianfan/ais/console/onlineService)
   e attiva l'llm service. bisogna passare per il real name authentication.

2. Per fare la query a Baidu bisogna ottenere una API Key e una Secret Key 
   * [Ottieni le Key](https://console.bce.baidu.com/iam/#/iam/accesslist).
   * Da questa pagina si possono ottenere anche API Key e Certificati

3. Con queste chiavi si può fare una query per ottenere un access token
   * [Ottieni l'Access Token](https://cloud.baidu.com/doc/WENXINWORKSHOP/s/Ilkkrb0i5)).

       curl -X POST 'https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=[API Key]&client_secret=[Secret Key]'  -H 'Content-Type: application/json'


# Modelli
* [Ernie 4.0 Turbo 128K](https://cloud.baidu.com/doc/WENXINWORKSHOP/s/7m0oog4ra)

      POST /rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-4.0-turbo-128k?access_token=24.4a3a19b******18992 HTTP/1.1
      Host: aip.baidubce.com
      Content-Type: application/json 
       { "messages": [
          {"role":"user","content":"介绍一下北京"}
       ] }


https://console.bce.baidu.com/ai_apaas/dialogHome?track=wendangzhongxin
Qui c'è l'API key = ...


https://ai.baidu.com/ai-doc/WENXINWORKSHOP/am3ih7xdy
Boh altra presentazione con documentazione e endpoint
Ernie 4.0 con docs e api c'è Java, ma non c'è curl.

Come ottenere Access e Secret Key:
https://cloud.baidu.com/doc/Reference/s/9jwvz2egb

