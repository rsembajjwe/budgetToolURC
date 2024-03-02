
package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.livedata.repository.SALFLDGRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SALFLDGService {

    private final SALFLDGRepository salfldgRepository;

    @Autowired
    public SALFLDGService(SALFLDGRepository salfldgRepository) {
        this.salfldgRepository = salfldgRepository;
    }
    public void getNothing(){
        
    }

}

