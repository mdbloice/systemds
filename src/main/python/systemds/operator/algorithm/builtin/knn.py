# -------------------------------------------------------------
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# -------------------------------------------------------------

# Autogenerated By   : src/main/python/generator/generator.py
# Autogenerated From : scripts/builtin/knn.dml

from typing import Dict, Iterable

from systemds.operator import OperationNode, Matrix, Frame, List, MultiReturn, Scalar
from systemds.script_building.dag import OutputType
from systemds.utils.consts import VALID_INPUT_TYPES


def knn(Train: Matrix,
        Test: Matrix,
        CL: Matrix,
        START_SELECTED: Matrix,
        **kwargs: Dict[str, VALID_INPUT_TYPES]):
    """
    :param CL_T: Y           The target type of matrix CL whether
    :param columns: are continuous ( =1 ) or
    :param trans_continuous: Y           Option flag for continuous feature transformed to [-1,1]:
    :param k_value: Y           k value for KNN, ignore if select_k enable
    :param select_k: Y           Use k selection algorithm to estimate k (TRUE means yes)
    :param k_min: Y           Min k value(  available if select_k = 1 )
    :param k_max: Y           Max k value(  available if select_k = 1 )
    :param select_feature: Y           Use feature selection algorithm to select feature (TRUE means yes)
    :param feature_max: Y           Max feature selection
    :param interval: Y           Interval value for K selecting (  available if select_k = 1 )
    :param feature_importance: Y           Use feature importance algorithm to estimate each feature
    :param predict_con_tg: Y           Continuous  target predict function: mean(=0) or median(=1)
    :return: 'OperationNode' containing  
    """
    params_dict = {'Train': Train, 'Test': Test, 'CL': CL, 'START_SELECTED': START_SELECTED}
    params_dict.update(kwargs)
    
    vX_0 = Matrix(Train.sds_context, '')
    vX_1 = Matrix(Train.sds_context, '')
    vX_2 = Matrix(Train.sds_context, '')
    output_nodes = [vX_0, vX_1, vX_2, ]

    op = MultiReturn(Train.sds_context, 'knn', output_nodes, named_input_nodes=params_dict)

    vX_0._unnamed_input_nodes = [op]
    vX_1._unnamed_input_nodes = [op]
    vX_2._unnamed_input_nodes = [op]

    return op
