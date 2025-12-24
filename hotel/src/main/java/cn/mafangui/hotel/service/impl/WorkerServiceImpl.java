package cn.mafangui.hotel.service.impl;

import cn.mafangui.hotel.entity.Worker;
import cn.mafangui.hotel.mapper.WorkerMapper;
import cn.mafangui.hotel.service.WorkerService;
import cn.mafangui.hotel.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerServiceImpl implements WorkerService {

    @Autowired
    private WorkerMapper workerMapper;

    @Override
    public int insert(Worker worker) {
        worker.setPassword(MD5Utils.MD5Encode(worker.getPassword()));
        return workerMapper.insertSelective(worker);
    }

    @Override
    public int delete(int workerId) {
        return workerMapper.deleteByPrimaryKey(workerId);
    }

    @Override
    public int updateById(Worker worker) {
        return workerMapper.updateByPrimaryKeySelective(worker);
    }

    @Override
    public Worker selectById(int workerId) {
        return workerMapper.selectByPrimaryKey(workerId);
    }

    @Override
    public Worker selectByUsername(String username) {
        return workerMapper.selectByUsername(username);
    }

    @Override
    public List<Worker> findAll() {
        return workerMapper.selectAll();
    }

    @Override
    public List<Worker> selectByRole(String role) {
        return workerMapper.selectByRole(role);
    }

    @Override
    public Worker login(String username, String password, String role) {
        Worker worker = workerMapper.selectByUsername(username);
        if (worker == null) {
            return null;
        }
        if (role != null && !role.equalsIgnoreCase(worker.getRole())) {
            return null;
        }
        return verifyAndUpgradePassword(worker, password);
    }

    @Override
    public Worker login(String username, String password) {
        Worker worker = workerMapper.selectByUsername(username);
        if (worker == null) {
            return null;
        }
        return verifyAndUpgradePassword(worker, password);
    }

    private Worker verifyAndUpgradePassword(Worker worker, String rawPassword) {
        String encodedPassword = MD5Utils.MD5Encode(rawPassword);
        if (encodedPassword.equalsIgnoreCase(worker.getPassword())) {
            return worker;
        }
        if (rawPassword.equals(worker.getPassword())) {
            Worker update = new Worker();
            update.setWorkerId(worker.getWorkerId());
            update.setPassword(encodedPassword);
            workerMapper.updateByPrimaryKeySelective(update);
            worker.setPassword(encodedPassword);
            return worker;
        }
        return null;
    }
}
