-- Seed Data for College Complaint & Feedback Management System

-- Departments
INSERT INTO departments (id, code, name, description) VALUES
(1, 'CSE', 'Computer Science & Engineering', 'Department of Computer Science and Engineering'),
(2, 'IT', 'Information Technology', 'Department of Information Technology'),
(3, 'MECH', 'Mechanical Engineering', 'Department of Mechanical Engineering'),
(4, 'CIVIL', 'Civil Engineering', 'Department of Civil Engineering'),
(5, 'ECE', 'Electronics & Communication', 'Department of Electronics and Communication Engineering'),
(6, 'ADMIN', 'Campus Administration', 'Office of Campus Administration & Estate Maintenance');

-- Admins (Default: superadmin / Admin@12345)
INSERT INTO admins (id, username, password_hash, full_name, email, role) VALUES
(1, 'superadmin', '$2a$12$DKbV3mY6yFJOoNuyG0rPhO6cCDA7urFo0ZHd0eRCCBt1GtZ0PvL.C', 'Chief Administrator', 'admin@campus.edu', 'SUPER_ADMIN'),
(2, 'warden_hostel', '$2a$12$DKbV3mY6yFJOoNuyG0rPhO6cCDA7urFo0ZHd0eRCCBt1GtZ0PvL.C', 'Hostel Warden', 'warden@campus.edu', 'ADMIN');

-- Demo Students (Default password: Student@123)
INSERT INTO students (id, roll_no, email, full_name, department_id, year_of_study, mobile, password_hash) VALUES
(1, '22CS101', 'student1@campus.edu', 'Aarav Sharma', 1, 3, '9876543210', '$2a$12$gUOe6nNAa3CfLKwXEVXmJepRsGOqePintWB.KkLYLrFbkoAvHmgMm'),
(2, '23IT042', 'student2@campus.edu', 'Ananya Patel', 2, 2, '9876543211', '$2a$12$gUOe6nNAa3CfLKwXEVXmJepRsGOqePintWB.KkLYLrFbkoAvHmgMm'),
(3, '21ME077', 'student3@campus.edu', 'Rohan Gupta', 3, 4, '9876543212', '$2a$12$gUOe6nNAa3CfLKwXEVXmJepRsGOqePintWB.KkLYLrFbkoAvHmgMm'),
(4, '24CV015', 'student4@campus.edu', 'Diya Sen', 4, 1, '9876543213', '$2a$12$gUOe6nNAa3CfLKwXEVXmJepRsGOqePintWB.KkLYLrFbkoAvHmgMm'),
(5, '22EC088', 'student5@campus.edu', 'Vikram Malhotra', 5, 3, '9876543214', '$2a$12$gUOe6nNAa3CfLKwXEVXmJepRsGOqePintWB.KkLYLrFbkoAvHmgMm');

-- Sample Complaints (~25 across statuses, priorities, and departments)
INSERT INTO complaints (id, tracking_id, student_id, student_name, roll_number, email, department_id, category, priority, subject, description, is_anonymous, status, attachment_path, internal_notes, public_remark, assigned_to, is_public, created_at, updated_at) VALUES
(1, 'CMP-2026-00001', 1, 'Aarav Sharma', '22CS101', 'student1@campus.edu', 1, 'IT/WiFi', 'High', 'WiFi router down in Lab 3', 'The main router in Turing Lab 3 has been unserviceable for 3 days. Students cannot access lab assignments or push code.', FALSE, 'Resolved', NULL, 'Replaced faulty Cisco AP switch.', 'Router hardware replaced and throughput tested at 100 Mbps.', 'Prof. K. Rao', TRUE, '2026-09-05 09:30:00', '2026-09-06 14:20:00'),
(2, 'CMP-2026-00002', 2, 'Ananya Patel', '23IT042', 'student2@campus.edu', 2, 'Hostel', 'Medium', 'Hot water geyser not working in Block B', 'Geyser on 2nd floor Block B hostel trips the circuit breaker whenever turned on.', FALSE, 'Resolved', NULL, 'Thermostat element replaced by electrician.', 'Electrician replaced the heating coil. Functioning normally now.', 'Estate Office', TRUE, '2026-09-08 11:15:00', '2026-09-09 17:00:00'),
(3, 'CMP-2026-00003', 3, 'Rohan Gupta', '21ME077', 'student3@campus.edu', 3, 'Infrastructure', 'High', 'Lathe machine chuck damaged in Workshop', 'Heavy vibration and cracked chuck on Lathe #4 in mechanical workshop. Poses safety hazard.', FALSE, 'In Progress', NULL, 'Chuck ordered from OEM. Awaiting delivery by Thursday.', 'Machine cordoned off. Replacement part in transit.', 'Workshop Supt.', FALSE, '2026-09-12 10:00:00', '2026-09-15 11:30:00'),
(4, 'CMP-2026-00004', 4, 'Diya Sen', '24CV015', 'student4@campus.edu', 4, 'Library', 'Low', 'Need more copies of Reinforced Concrete Design', 'Only 2 copies available in reference section for semester 5 syllabus. Both are permanently checked out.', FALSE, 'Under Review', NULL, 'Checking departmental library budget allocation.', 'Request forwarded to Library Advisory Committee.', 'Chief Librarian', FALSE, '2026-09-15 14:00:00', '2026-09-16 09:45:00'),
(5, 'CMP-2026-00005', 5, 'Vikram Malhotra', '22EC088', 'student5@campus.edu', 5, 'Academics', 'Medium', 'Elective clash between VLSI and Embedded Systems', 'Both elective courses are scheduled at 10:00 AM on Tuesdays and Thursdays in the master timetable.', FALSE, 'Resolved', NULL, 'Timetable rescheduled for slot D.', 'Timetable committee moved Embedded Systems to Slot E. Updated timetable posted.', 'Dean Academics', TRUE, '2026-09-18 16:30:00', '2026-09-20 10:15:00'),
(6, 'CMP-2026-00006', 1, 'Aarav Sharma', '22CS101', 'student1@campus.edu', 1, 'Canteen', 'Medium', 'Drinking water cooler filtration issue', 'The cooler outside the cafeteria has yellow sediment and tastes foul. Needs urgent filter replacement.', FALSE, 'Resolved', NULL, 'RO filters replaced and tank sanitized.', 'RO membranes changed, water TDS verified at 85 ppm.', 'Health Inspector', TRUE, '2026-09-22 13:00:00', '2026-09-23 16:00:00'),
(7, 'CMP-2026-00007', 1, 'Aarav Sharma', '22CS101', 'student1@campus.edu', 6, 'Transport', 'High', 'Bus route #12 reckless speeding', 'Driver of bus #12 consistently skips stops and overspeeds near North Gate roundabout.', TRUE, 'In Progress', NULL, 'Transport in-charge called contractor.', 'Driver summoned for counseling and speed governor inspection initiated.', 'Transport Manager', FALSE, '2026-09-25 08:45:00', '2026-09-26 12:00:00'),
(8, 'CMP-2026-00008', 2, 'Ananya Patel', '23IT042', 'student2@campus.edu', 2, 'IT/WiFi', 'Medium', 'Projector flickering in Room 402', 'HDMI connector cable is frayed and projector bulb keeps flashing during seminar presentations.', FALSE, 'Resolved', NULL, 'Replaced HDMI drop cable and secured ceiling mount.', 'New HDMI cable installed and tested.', 'AV Support', TRUE, '2026-09-27 15:20:00', '2026-09-28 11:10:00'),
(9, 'CMP-2026-00009', 3, 'Rohan Gupta', '21ME077', 'student3@campus.edu', 3, 'Hostel', 'Low', 'Washing machine coin slot jammed', 'Laundry room machine #2 in girls hostel swallowed token without starting cycle.', FALSE, 'Resolved', NULL, 'Coin mechanism cleaned and token refunded to student.', 'Coin acceptor serviced; student collected replacement token from warden.', 'Hostel Warden', FALSE, '2026-09-29 19:10:00', '2026-09-30 10:00:00'),
(10, 'CMP-2026-00010', 1, 'Aarav Sharma', '22CS101', 'student1@campus.edu', 1, 'Infrastructure', 'High', 'Water leakage near Server Room rack 2', 'Rain seepage from ceiling joint near server room wall. Could damage networking racks if rainfall continues.', FALSE, 'In Progress', NULL, 'Estate team applied temporary waterproof sealant. Permanent roof repair scheduled.', 'Emergency sealant applied. Estate contractor working on roof terrace.', 'Estate Engineer', FALSE, '2026-09-30 08:30:00', '2026-10-01 09:00:00'),
(11, 'CMP-2026-00011', 5, 'Vikram Malhotra', '22EC088', 'student5@campus.edu', 5, 'Library', 'Low', 'Study room AC temperature set too low (16C)', 'Central AC in 1st floor quiet study area is freezing and remote control is missing.', FALSE, 'Resolved', NULL, 'Thermostat adjusted to 24C.', 'AC temperature calibrated to comfortable 24 degrees Celsius.', 'Library Attendant', TRUE, '2026-10-01 11:00:00', '2026-10-01 14:00:00'),
(12, 'CMP-2026-00012', 4, 'Diya Sen', '24CV015', 'student4@campus.edu', 4, 'Academics', 'Medium', 'Surveying equipment calibration certificate expired', 'Total stations in Civil surveying lab show significant angular deviations.', FALSE, 'Under Review', NULL, 'Sent inquiry to surveying equipment vendor for annual maintenance calibration.', 'Vendor contacted for calibration schedule.', 'Lab In-charge', FALSE, '2026-10-01 15:00:00', '2026-10-02 10:30:00'),
(13, 'CMP-2026-00013', 2, 'Ananya Patel', '23IT042', 'student2@campus.edu', 6, 'Ragging/Harassment', 'High', 'Verbal harassment near hostel mess pathway late night', 'Senior students gathering near mess alleyway after 10 PM and passing derogatory remarks to freshers.', TRUE, 'Under Review', NULL, 'Anti-ragging committee alerted. Night patrol logs requested.', 'Anti-Ragging Squad assigned for strict nocturnal surveillance on the reported route.', 'Anti-Ragging Squad', FALSE, '2026-10-02 22:15:00', '2026-10-03 08:30:00'),
(14, 'CMP-2026-00014', 2, 'Ananya Patel', '23IT042', 'student2@campus.edu', 2, 'Canteen', 'Low', 'Lack of healthy fruit/salad options in cafeteria', 'Only fried items available during evening tea break. Requesting inclusion of fruit bowls or sandwiches.', FALSE, 'Submitted', NULL, NULL, NULL, NULL, FALSE, '2026-10-03 10:00:00', '2026-10-03 10:00:00'),
(15, 'CMP-2026-00015', 3, 'Rohan Gupta', '21ME077', 'student3@campus.edu', 3, 'IT/WiFi', 'Medium', 'CAD/CAM Lab license server down', 'SolidWorks and ANSYS throwing network license error code 15.', FALSE, 'In Progress', NULL, 'FLEXnet license service restarted. Verifying client seat checkouts.', 'License server restarted; investigating firewall port rule.', 'SysAdmin', FALSE, '2026-10-03 11:30:00', '2026-10-03 14:00:00'),
(16, 'CMP-2026-00016', 1, 'Aarav Sharma', '22CS101', 'student1@campus.edu', 1, 'Infrastructure', 'High', 'Broken stair tread in Science Block B', 'Top marble step on staircase leading to 3rd floor is loose. Trip hazard.', FALSE, 'Submitted', NULL, NULL, NULL, NULL, FALSE, '2026-10-03 13:10:00', '2026-10-03 13:10:00'),
(17, 'CMP-2026-00017', 5, 'Vikram Malhotra', '22EC088', 'student5@campus.edu', 5, 'Transport', 'Low', 'Bicycle stand roofing damaged', 'Wind gust ripped tin sheet off bicycle stand near gate 2.', FALSE, 'Submitted', NULL, NULL, NULL, NULL, FALSE, '2026-10-03 16:00:00', '2026-10-03 16:00:00'),
(18, 'CMP-2026-00018', 4, 'Diya Sen', '24CV015', 'student4@campus.edu', 4, 'Hostel', 'High', 'Water supply pressure low on 4th floor', 'Taps running dry on 4th floor East Wing during morning peak hours (7-9 AM).', FALSE, 'Under Review', NULL, 'Booster pump pressure switch may be faulty.', 'Estate maintenance checking overhead supply pump.', 'Estate Team', FALSE, '2026-10-04 07:30:00', '2026-10-04 09:00:00'),
(19, 'CMP-2026-00019', 2, 'Ananya Patel', '23IT042', 'student2@campus.edu', 2, 'Academics', 'Low', 'Delay in uploading lecture notes for Data Structures', 'Module 3 notes still pending on campus LMS portal.', FALSE, 'Submitted', NULL, NULL, NULL, NULL, FALSE, '2026-10-04 10:15:00', '2026-10-04 10:15:00'),
(20, 'CMP-2026-00020', 3, 'Rohan Gupta', '21ME077', 'student3@campus.edu', 3, 'Library', 'Low', 'Digital library terminal #5 mouse unresponsive', 'USB optical mouse optical sensor not tracking.', FALSE, 'Submitted', NULL, NULL, NULL, NULL, FALSE, '2026-10-04 11:45:00', '2026-10-04 11:45:00'),
(21, 'CMP-2026-00021', 1, 'Aarav Sharma', '22CS101', 'student1@campus.edu', 1, 'Other', 'Medium', 'Gym treadmill emergency stop key broken', 'Treadmill 1 cannot be operated safely without safety lanyard pin.', FALSE, 'Submitted', NULL, NULL, NULL, NULL, FALSE, '2026-10-04 12:20:00', '2026-10-04 12:20:00'),
(22, 'CMP-2026-00022', 5, 'Vikram Malhotra', '22EC088', 'student5@campus.edu', 5, 'Infrastructure', 'High', 'Ceiling fan making screeching noise in LH-101', 'Bearings worn out in central fan of lecture hall 101, disrupts lecture audio.', FALSE, 'Submitted', NULL, NULL, NULL, NULL, FALSE, '2026-10-04 13:00:00', '2026-10-04 13:00:00'),
(23, 'CMP-2026-00023', 4, 'Diya Sen', '24CV015', 'student4@campus.edu', 4, 'Canteen', 'Medium', 'Card machine declining UPI repeatedly at counter 2', 'POS machine network timeout keeps debiting student accounts while showing failed.', FALSE, 'Under Review', NULL, 'Informed bank merchant support team.', 'Merchant terminal being updated by payment provider.', 'Canteen Manager', FALSE, '2026-10-04 13:40:00', '2026-10-04 14:15:00'),
(24, 'CMP-2026-00024', 2, 'Ananya Patel', '23IT042', 'student2@campus.edu', 2, 'Hostel', 'High', 'Streetlight flickering outside Girls Hostel gate', 'Dark blind spot on connecting road due to flickering sodium lamp.', FALSE, 'Submitted', NULL, NULL, NULL, NULL, FALSE, '2026-10-04 14:10:00', '2026-10-04 14:10:00'),
(25, 'CMP-2026-00025', 3, 'Rohan Gupta', '21ME077', 'student3@campus.edu', 3, 'Academics', 'Medium', 'Attendance discrepancy in Industrial Metallurgy', 'Portal shows 68% attendance despite having attended all tutorial batches with biometric punch.', FALSE, 'Submitted', NULL, NULL, NULL, NULL, FALSE, '2026-10-04 14:35:00', '2026-10-04 14:35:00');

-- Status Logs for realistic audit history
INSERT INTO status_logs (complaint_id, old_status, new_status, changed_by, remark, created_at) VALUES
(1, NULL, 'Submitted', 'Aarav Sharma', 'Complaint filed.', '2026-09-05 09:30:00'),
(1, 'Submitted', 'Under Review', 'superadmin', 'Forwarded to IT infrastructure technician.', '2026-09-05 11:00:00'),
(1, 'Under Review', 'In Progress', 'Prof. K. Rao', 'Diagnosing hardware switch.', '2026-09-05 14:30:00'),
(1, 'In Progress', 'Resolved', 'Prof. K. Rao', 'Router hardware replaced and throughput tested at 100 Mbps.', '2026-09-06 14:20:00'),
(2, NULL, 'Submitted', 'Ananya Patel', 'Complaint filed.', '2026-09-08 11:15:00'),
(2, 'Submitted', 'In Progress', 'warden_hostel', 'Electrician assigned.', '2026-09-08 13:00:00'),
(2, 'In Progress', 'Resolved', 'warden_hostel', 'Electrician replaced the heating coil. Functioning normally now.', '2026-09-09 17:00:00'),
(3, NULL, 'Submitted', 'Rohan Gupta', 'Complaint filed.', '2026-09-12 10:00:00'),
(3, 'Submitted', 'Under Review', 'superadmin', 'Sent to workshop superintendent.', '2026-09-13 09:00:00'),
(3, 'Under Review', 'In Progress', 'Workshop Supt.', 'Machine cordoned off. Replacement part in transit.', '2026-09-15 11:30:00'),
(5, NULL, 'Submitted', 'Vikram Malhotra', 'Complaint filed.', '2026-09-18 16:30:00'),
(5, 'Submitted', 'Under Review', 'superadmin', 'Reviewed by academic council.', '2026-09-19 10:00:00'),
(5, 'Under Review', 'Resolved', 'Dean Academics', 'Timetable committee moved Embedded Systems to Slot E. Updated timetable posted.', '2026-09-20 10:15:00'),
(6, NULL, 'Submitted', 'Aarav Sharma', 'Complaint filed.', '2026-09-22 13:00:00'),
(6, 'Submitted', 'In Progress', 'superadmin', 'Plumbing team dispatched.', '2026-09-22 15:30:00'),
(6, 'In Progress', 'Resolved', 'Health Inspector', 'RO membranes changed, water TDS verified at 85 ppm.', '2026-09-23 16:00:00'),
(7, NULL, 'Submitted', 'Anonymous Student', 'Complaint filed.', '2026-09-25 08:45:00'),
(7, 'Submitted', 'In Progress', 'superadmin', 'Driver summoned for counseling and speed governor inspection initiated.', '2026-09-26 12:00:00'),
(10, NULL, 'Submitted', 'Aarav Sharma', 'Complaint filed.', '2026-09-30 08:30:00'),
(10, 'Submitted', 'In Progress', 'superadmin', 'Emergency sealant applied. Estate contractor working on roof terrace.', '2026-10-01 09:00:00'),
(13, NULL, 'Submitted', 'Anonymous Student', 'Complaint filed.', '2026-10-02 22:15:00'),
(13, 'Submitted', 'Under Review', 'superadmin', 'Anti-Ragging Squad assigned for strict nocturnal surveillance on the reported route.', '2026-10-03 08:30:00');

-- Notifications for demo students
INSERT INTO notifications (id, student_id, complaint_id, message, is_read, created_at) VALUES
(1, 1, 1, 'Your complaint CMP-2026-00001 (WiFi router down in Lab 3) was marked as Resolved.', FALSE, '2026-09-06 14:20:00'),
(2, 1, 10, 'Your complaint CMP-2026-00010 (Water leakage near Server Room) moved to In Progress.', FALSE, '2026-10-01 09:00:00'),
(3, 2, 2, 'Your complaint CMP-2026-00002 (Hot water geyser not working) was marked as Resolved.', FALSE, '2026-09-09 17:00:00'),
(4, 2, 13, 'Your anonymous report CMP-2026-00013 has been placed Under Review.', FALSE, '2026-10-03 08:30:00'),
(5, 3, 3, 'Your complaint CMP-2026-00003 (Lathe machine chuck damaged) moved to In Progress.', FALSE, '2026-09-15 11:30:00');

-- Feedback entries (~15 across departments with varied ratings)
INSERT INTO feedback (department_id, student_id, category, rating, comment, created_at) VALUES
(1, 1, 'Lab Facilities', 5, 'The new Linux dual-boot setup in Lab 2 is extremely fast. Great improvement!', '2026-09-10 14:22:00'),
(1, 1, 'Faculty Guidance', 4, 'DSA lab instructors are patient and encourage peer coding.', '2026-09-14 16:45:00'),
(2, 2, 'WiFi & Network', 3, 'Network speed is good in the morning but throttles around 4 PM.', '2026-09-16 17:30:00'),
(2, 2, 'Curriculum', 4, 'Cloud Computing hands-on workshop was very informative.', '2026-09-20 12:10:00'),
(3, 3, 'Workshop Equipment', 4, 'CNC simulation software helped us prepare well before actual machining.', '2026-09-22 11:00:00'),
(3, 3, 'Safety Standards', 5, 'Appreciate the mandatory safety goggles and protective apron enforcement.', '2026-09-24 15:15:00'),
(4, 4, 'Surveying Camps', 5, 'The 3-day field surveying camp at Lakeview ridge was very well organized.', '2026-09-25 18:00:00'),
(4, 4, 'Classroom Facilities', 3, 'Acoustics in Room 204 need improvement; backbenchers struggle to hear.', '2026-09-28 10:30:00'),
(5, 5, 'Robotics Lab', 5, 'Microcontroller kits with STM32 and Arduino are brand new and fully functional.', '2026-09-29 14:40:00'),
(5, 5, 'Study Material', 4, 'Digital Signal Processing tutorial question banks with step-by-step solutions are very helpful.', '2026-10-01 13:20:00'),
(6, 1, 'Library Timings', 5, 'Extending quiet reading room hours until 11 PM during exam weeks is a lifesaver!', '2026-10-02 16:00:00'),
(6, 2, 'Cafeteria Quality', 3, 'South Indian breakfast items are delicious, but lunch thali options get repetitive.', '2026-10-03 13:30:00'),
(6, 3, 'Campus Cleanliness', 5, 'Campus lawn and waste segregation bins are maintained spotless every single morning.', '2026-10-03 17:45:00'),
(1, 1, 'Hackathon Support', 5, 'Department sponsored our hackathon team travel and registration without red tape.', '2026-10-04 11:10:00'),
(6, 4, 'Administrative Office', 4, 'Bonafide certificate issuance took less than 24 hours through the digital desk.', '2026-10-04 12:50:00');
