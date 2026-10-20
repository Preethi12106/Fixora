USE hostel_complaint_db;

INSERT INTO complaints (title, description, category, room_number, status, created_at)
VALUES
    ('Light issue', 'Main light in room 101 is flickering.', 'Electrical', '101', 'PENDING', NOW()),
    ('Drain blocked', 'Bathroom drain is blocked for the last two days.', 'Plumbing', '305', 'RESOLVED', NOW()),
    ('Dustbin not cleaned', 'Dustbin in the corridor is overflowing daily.', 'Cleanliness', 'B-08', 'IN_PROGRESS', NOW());

INSERT INTO feedbacks (rating, comments, created_at)
VALUES
    (3, 'The food is okay but the room ventilation can be improved.', NOW()),
    (5, 'Excellent maintenance support from the hostel team.', NOW());
