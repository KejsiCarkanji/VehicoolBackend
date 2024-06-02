package com.vehicool.vehicool.business.service;

import com.vehicool.vehicool.persistence.entity.Contract;
import com.vehicool.vehicool.persistence.repository.ContractRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;

    public Contract getContractById(Long id) {
        return contractRepository.findById(id).orElse(null);
    }

    public Contract save(Contract contract) {
        return contractRepository.save(contract);
    }

    public void delete(Long id){
        contractRepository.deleteById(id);
    }

    public Contract update(Contract contract,Long Id){
        contract.setId(Id);
        return contractRepository.saveAndFlush(contract);
    }


}
