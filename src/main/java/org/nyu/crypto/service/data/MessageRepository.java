package org.nyu.crypto.service.data;


import org.nyu.crypto.dto.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends CrudRepository<Message, UUID> { }
