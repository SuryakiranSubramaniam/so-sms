{"items":[<#list>{"Source_Country":"${body.Source_Country}","Contact_Key":"${body.Contact_Key}","Title":"${body.Title}","First_Name":"${body.First_Name}","Last_Name":"${body.Last_Name}","Email_Address":"${body.Email_Address}","Mobile_Number":"${body.Mobile_Number}","Mobile_Locale":"${body.Mobile_Locale}","Is_Deleted":"${body.Is_Deleted}","Hash_Key":"${body.Hash_Key}","offer_id":"${body.offer_id}","Product_Name":"${body.Product_Name}","Premium_Selected":"${body.Premium_Selected}","campaign_end_date":"${body.campaign_end_date}","Coverage":"${body.Coverage}","UTM_SOURCE":"${body.UTM_SOURCE}","UTM_CAMPAIGN":"${body.UTM_CAMPAIGN}","UTM_SRC":"${body.UTM_SRC}","FLOW_ID":"${body.FLOW_ID}"}<#sep>,</#list>]}${request.setHeader('CamelHttpMethod','PUT')}${request.setHeader('Content-Type','application/json')}
