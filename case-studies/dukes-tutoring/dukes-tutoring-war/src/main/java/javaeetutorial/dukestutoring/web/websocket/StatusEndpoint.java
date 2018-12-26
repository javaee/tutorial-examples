/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package javaeetutorial.dukestutoring.web.websocket;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.dukestutoring.ejb.RequestBean;
import javaeetutorial.dukestutoring.entity.Student;
import javaeetutorial.dukestutoring.events.StatusEvent;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/status")
@Dependent
public class StatusEndpoint {

    @EJB
    RequestBean requestBean;
    private static final Logger log = Logger.getLogger("StatusEndpoint");
    private static final Set<Session> sessions =
            Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void open(final Session session) {
        sessions.add(session);
        /* Send list of students */
        List<Student> students = requestBean.getAllStudents();
        String studentList = jsonStudentList(students);
        try {
            session.getBasicRemote().sendText(studentList);
        } catch (IOException e) {
            log.log(Level.INFO, "[StatusEndpoint] {0}", e.getMessage());
        }
    }

    @OnClose
    public void close(final Session session) {
        sessions.remove(session);
    }

    public static synchronized void updateStatus(@Observes StatusEvent event) {
        log.info("updateStatus");
        for (Session s : sessions) {
            if (s.isOpen()) {
                try {
                    String studentUpdate = jsonStudentUpdate(event.getStudent());
                    s.getBasicRemote().sendText(studentUpdate);
                    log.log(Level.INFO, "[StatusEndpoint] {0} is now {1}", 
                            new Object[]{event.getStudent().getName(), 
                                event.getStudent().getStatus()});
                    /* Send update */
                } catch (IOException e) {
                    log.log(Level.INFO, "[StatusEndpoint] {0}", e.getMessage());
                }
            }
        }
    }
    public static synchronized void updateStatus2(Student student) {
        log.info("updateStatus2");
        for (Session s : sessions) {
            if (s.isOpen()) {
                try {
                    String studentUpdate = jsonStudentUpdate(student);
                    s.getBasicRemote().sendText(studentUpdate);
                    log.log(Level.INFO, "[StatusEndpoint] {0} is now {1}", 
                            new Object[]{student.getName(), 
                                student.getStatus()});
                    /* Send update */
                } catch (IOException e) {
                    log.log(Level.INFO, "[StatusEndpoint] {0}", e.getMessage());
                }
            }
        }
    }
    /* One list message to start
     * {
     *   "type":"list",
     *   "body": 
     *     [
     *       {"name":"StudentName1", "status":"StudentStatus1"},
     *       {"name":"StudentName2", "status":"StudentStatus2"},
     *       ...
     *     ]
     * }
     */

    private static String jsonStudentList(List<Student> students) {

        StringWriter swriter = new StringWriter();
        try (JsonGenerator gen = Json.createGenerator(swriter)) {
            gen.writeStartObject();
            gen.write("type", "list");
            gen.writeStartArray("body");
            for (Student student : students) {
                gen.writeStartObject();
                gen.write("name", student.getName());
                gen.write("status", student.getStatus().toString());
                gen.writeEnd();
            }
            gen.writeEnd();
            gen.writeEnd();
        }

        return swriter.toString();
    }

    /* One update message per student update:
     * {
     *   "type":"update",
     *   "body": {
     *     "name":"StudentName1", 
     *     "status":"StudentStatus1"
     *   }
     * }
     */
    private static String jsonStudentUpdate(Student student) {

        StringWriter swriter = new StringWriter();
        try (JsonGenerator gen = Json.createGenerator(swriter)) {
            gen.writeStartObject();
            gen.write("type", "update");
            gen.writeStartObject("body");
            gen.write("name", student.getName());
            gen.write("status", student.getStatus().toString());
            gen.writeEnd();
            gen.writeEnd();
        }

        return swriter.toString();
    }
}
