package kr.co.popool.bblmember.service;

import kr.co.popool.bblmember.infra.error.exception.BadRequestException;
import kr.co.popool.bblmember.infra.error.exception.NotFoundException;
import kr.co.popool.bblmember.infra.interceptor.IdentityThreadLocal;
import kr.co.popool.bblmember.persistence.entity.CareerEntity;
import kr.co.popool.bblmember.persistence.entity.MemberEntity;
import kr.co.popool.bblmember.persistence.repository.CareerRepository;
import kr.co.popool.bblmember.persistence.repository.MemberRepository;
import kr.co.popool.bblmember.service.model.dtos.CareerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerService {

    private final CareerRepository careerRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createCareer(CareerDto.CREATE create) {
        final MemberEntity memberEntity = getThreadLocal();
        final CareerEntity careerEntity = CareerEntity.toCareerEntity(create, memberEntity);

        careerRepository.save(careerEntity);
    }

    public CareerDto.READ getCareer(Long careerId) {
        final MemberEntity memberEntity = getThreadLocal();
        final CareerEntity careerEntity = careerRepository.findById(careerId)
                .orElseThrow(() -> new NotFoundException(memberEntity.getName() + "회원의 이력서가 존재하지 않습니다."));

        return CareerEntity.toReadDto(careerEntity);
    }

    public List<CareerDto.READ> getAllCareer() {
        return careerRepository.findAll().stream().map(CareerEntity::toReadDto).collect(Collectors.toList());
    }

    @Transactional
    public void updateCareer(CareerDto.UPDATE update) {
        final MemberEntity memberEntity = getThreadLocal();

        CareerEntity careerEntity = careerRepository.findById(update.getCareerId())
                .orElseThrow(() -> new NotFoundException(memberEntity.getName() + "해당 회원 이력서는 존재하지 않습니다."));

        careerEntity.updateCareer(update);
        careerRepository.save(careerEntity);
    }

    @Transactional
    public void deleteCareer(Long careerId) {
        final MemberEntity memberEntity = getThreadLocal();
        CareerEntity careerEntity = careerRepository.findById(careerId)
                .orElseThrow(() -> new NotFoundException(memberEntity.getName() + "해당 회원 이력서는 존재하지 않습니다."));

        careerRepository.delete(careerEntity);
    }

    private MemberEntity getThreadLocal() {
        String identity = IdentityThreadLocal.get();
        return memberRepository.findByIdentity(identity)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다."));
    }
}