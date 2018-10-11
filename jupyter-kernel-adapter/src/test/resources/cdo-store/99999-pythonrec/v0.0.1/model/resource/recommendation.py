def dosingrecommendation (inputs):




// KGrid CPIC guidelines HLA-B gene to abacavir Recommendation
genes = {'HLA-B':{}}
drug = 'allopurinol'
var allele = '58:01'
var keysuffix= {negative:'noncarrier', positive:'carrier'}
// var keymap = {'hla-b57:01noncarrier':'negative',"hla-b57:01carrier":'positive'}
// # Dictionary containing Phenotype to Recommendation Information
// var output =   { "type":"CPIC Recommendation","drug":"allopurinol","genes":{"HLA-B":{}}, "recommendation":{"classification":"",  "content":"","implication":""}}
var output =   {}
var recommendations = {
  'hla-b58:01noncarrier': {'implication': 'Low or reduced risk of allopurinol-induced SCAR',
          'recommendation': 'Use allopurinol per standard dosing guidelines',
          'classification': 'Strong'},
  'hla-b58:01carrier': {'implication': 'Significantly increased risk of allopurinol-induced SCAR',
                  'recommendation': 'Allopurinol is contraindicated',
                  'classification': 'Strong'}}
