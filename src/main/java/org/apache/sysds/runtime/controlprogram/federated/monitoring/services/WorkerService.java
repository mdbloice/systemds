/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysds.runtime.controlprogram.federated.monitoring.services;

import org.apache.sysds.runtime.DMLRuntimeException;
import org.apache.sysds.runtime.controlprogram.federated.FederatedData;
import org.apache.sysds.runtime.controlprogram.federated.FederatedRequest;
import org.apache.sysds.runtime.controlprogram.federated.FederatedResponse;
import org.apache.sysds.runtime.controlprogram.federated.FederatedStatistics;
import org.apache.sysds.runtime.controlprogram.federated.monitoring.models.BaseEntityModel;
import org.apache.sysds.runtime.controlprogram.federated.monitoring.repositories.DerbyRepository;
import org.apache.sysds.runtime.controlprogram.federated.monitoring.repositories.EntityEnum;
import org.apache.sysds.runtime.controlprogram.federated.monitoring.repositories.IRepository;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Future;

public class WorkerService {
	private static final IRepository _entityRepository = new DerbyRepository();

	public void create(BaseEntityModel model) {
		_entityRepository.createEntity(EntityEnum.WORKER, model);
	}

	public BaseEntityModel get(Long id) {
		var model = _entityRepository.getEntity(EntityEnum.WORKER, id);

		try {
			var statisticsResponse = getWorkerStatistics(model.getAddress()).get();

			if (statisticsResponse.isSuccessful()) {
				FederatedStatistics.FedStatsCollection aggFedStats = new FederatedStatistics.FedStatsCollection();

				Object[] tmp = statisticsResponse.getData();
				if(tmp[0] instanceof FederatedStatistics.FedStatsCollection)
					aggFedStats.aggregate((FederatedStatistics.FedStatsCollection)tmp[0]);

				var statsStr = FederatedStatistics.displayStatistics(aggFedStats, 5);
				model.setData(statsStr);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return model;
	}

	public List<BaseEntityModel> getAll() {
		return _entityRepository.getAllEntities(EntityEnum.WORKER);
	}

	private Future<FederatedResponse> getWorkerStatistics(String address) {
		Future<FederatedResponse> result = null;

		String host = address.split(":")[0];
		int port = Integer.parseInt(address.split(":")[1]);

		InetSocketAddress isa = new InetSocketAddress(host, port);
		FederatedRequest frUDF = new FederatedRequest(FederatedRequest.RequestType.EXEC_UDF, -1,
				new FederatedStatistics.FedStatsCollectFunction());
		try {
			result = FederatedData.executeFederatedOperation(isa, frUDF);
		} catch(DMLRuntimeException dre) {
			// silently ignore this exception --> caused by offline federated workers
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}
}
