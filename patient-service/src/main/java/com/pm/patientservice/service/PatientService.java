package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {


    //Dependecy injection same as @Autowired
    private final PatientRepository patientRepository;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository,KafkaProducer kafkaProducer){
        this.patientRepository=patientRepository;
        this.kafkaProducer=kafkaProducer;

    }

    @Autowired
    private  BillingServiceGrpcClient billingServiceGrpcClient;



    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients=patientRepository.findAll();

        return patients.stream().map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new
                    EmailAlreadyExistsException("Patient with this email already exist" + patientRequestDTO.getEmail());
        }

        // 1. DTO ko Model mein badlo aur save karo (newPatient ek model hai)
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));


      //  model ko pass kara ke as argument uska BillingAccount bana rahe
        //since model bana rahe toh uski id UUID format me isliye toString use kiya
        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),newPatient.getName(),newPatient.getEmail());

        //kafka ke through notification  bhj rahe
kafkaProducer.sendEvent(newPatient);

        // 2. Saved Entity ko wapas DTO mein badalkar return karo
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id,PatientRequestDTO patientRequestDTO){

        Patient patient=patientRepository.findById(id).orElseThrow(()->new PatientNotFoundException("Patient not found by Id"+ id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),
                id)) {
            throw new EmailAlreadyExistsException(
                    "A patient with this email " + "already exists"
                            + patientRequestDTO.getEmail());
        }
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id){
        patientRepository.deleteById(id);
    }

}
