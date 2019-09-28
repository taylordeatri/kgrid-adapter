
import os
import warnings
import sys
from pathlib import Path

import pandas as pd
import numpy as np
from itertools import cycle
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score
from sklearn.model_selection import train_test_split
from sklearn.linear_model import ElasticNet
from sklearn.linear_model import lasso_path, enet_path
from sklearn import datasets

import mlflow
import mlflow.sklearn





from mlflow.utils import PYTHON_VERSION
from mlflow.utils.file_utils import TempDir
from mlflow.utils.environment import _mlflow_conda_env




class SkLearnDiabetesPyfunc:
    def __init__(self, d_mean, d_std, sklearn_model):
        self.d_mean = d_mean
        self.d_std = d_std
        self.sklearn_model = sklearn_model
    
    def predict(self, input):
        """
        Generate predictions for the data.
        :param input: pandas.DataFrame with columns that represent the following features:
                  "columns": [
                    "age",
                    "sex",
                    "bmi",
                    "bp",
                    "s1",
                    "s2",
                    "s3",
                    "s4",
                    "s5",
                    "s6"
                  ],
                  "data": [
                    [
                        "60",
                        "2",
                        "22.3",
                        "113",
                        "186",
                        "125.8",
                        "46",
                        "4",
                        "4.2627",
                        "94"
                    ]
                  ]
        :return: Predicted progression: float,
        """
        dfNorm = self._normalize(input)
        
        print(dfNorm)
        
        predictedValue = self.sklearn_model.predict(dfNorm)
        return predictedValue
        

    def _normalize(self, df):
        dfN = (df - self.d_mean)/self.d_std
        return dfN
        
        
        

def _load_pyfunc(path):
        #with open(os.path.join(path, "conf.yaml"), "r") as f:
        #    conf = yaml.safe_load(f)
        #conf = yaml.safe_load(f)
        d_mean = pd.read_csv(os.path.join(path, "trained_mean.csv"), header=None, index_col=0, squeeze =True)
        d_std = pd.read_csv(os.path.join(path,"trained_std.csv"), header=None, index_col=0, squeeze =True)
        skmodel_path = os.path.join(path, "sklearn_model")
        sklearn_model = mlflow.sklearn.load_model(skmodel_path)
        return SkLearnDiabetesPyfunc(d_mean, d_std, sklearn_model)
        
        
        
def log_model(sk_model, path, train_mean, train_std):
    with TempDir() as tmp:
        data_path = tmp.path("diabetes_model")
        
        
        print("data_path = %s" % data_path)
        
        if os.path.exists(data_path):
             raise MlflowException(message="Path '{}' already exists".format(path),
                                   error_code=RESOURCE_ALREADY_EXISTS)
        os.mkdir(data_path)

        sklearn_model_path = os.path.join(data_path, "sklearn_model")
        mlflow.sklearn.save_model(sk_model, path=sklearn_model_path)
        
        conda_env = tmp.path("conda_env.yaml")
        with open(conda_env, "w") as f:
            f.write(conda_env_template.format(python_version=PYTHON_VERSION, mlflow_version=mlflow.__version__))

        train_mean = train_mean.drop(["y"])
        mean_data_subpath = "trained_mean.csv"
        train_mean.to_csv(os.path.join(data_path, mean_data_subpath), header=False, index=True)

        train_std = train_std.drop(["y"])
        std_data_subpath = "trained_std.csv"
        train_std.to_csv(os.path.join(data_path, std_data_subpath), header=False, index=True)
                
        mlflow.pyfunc.log_model(artifact_path = path,
                                loader_module=__name__,
                                code_path=[__file__],
                                data_path=data_path,
                                conda_env=conda_env
                               )
    
                    
                    
conda_env_template = """
name: diabetes_progression_predictor
channels:
    - defaults
dependencies:
    - python={python_version}
    - scikit-learn=0.21.2
    - matplotlib
    - pip:
        - mlflow
        - cloudpickle==1.2.1
"""

