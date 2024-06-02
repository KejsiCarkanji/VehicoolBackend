package com.vehicool.vehicool.business.service;

import com.vehicool.vehicool.persistence.entity.DataPool;
import com.vehicool.vehicool.persistence.repository.DataPoolRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DataPoolService {
    private final DataPoolRepository dataPoolRepository;

    public DataPool getDataPoolById(Long id) {
        return dataPoolRepository.findById(id).orElse(null);
    }

    public DataPool save(DataPool dataPool) {
        return dataPoolRepository.save(dataPool);
    }

    public void delete(Long id){
        dataPoolRepository.deleteById(id);
    }

    public DataPool update(DataPool dataPool,Long Id){
        dataPool.setId(Id);
        return dataPoolRepository.saveAndFlush(dataPool);
    }
    public List<DataPool> findAllByEnumName(String enumName){
        return dataPoolRepository.findAllByEnumName(enumName);
    }

    public DataPool findByEnumLabel(String enumLabel){
        return dataPoolRepository.findByEnumLabel(enumLabel);
    }
    public Long totalElements(){
        return dataPoolRepository.count();
    }


}
