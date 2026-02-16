

const mysql = require('mysql2');
require('dotenv').config();

const pool = mysql.createPool({
    host: process.env.DB_HOST,           // Database host (localhost)
    user: process.env.DB_USER,           // MySQL username (root)
    password: process.env.DB_PASSWORD,   // MySQL password (empty for local)
    database: process.env.DB_NAME,       // Database name (doctor_discovery)
    port: process.env.DB_PORT,           // MySQL port (3306 default)
    waitForConnections: true,            // Wait for available connection if pool is full
    connectionLimit: 10,                 // Maximum number of connections in pool
    queueLimit: 0                        // No limit on queued connection requests
});

/**
 * Convert pool to use Promises instead of callbacks
 * This allows us to use async/await syntax in our routes
 */
const promisePool = pool.promise();


promisePool.query('SELECT 1')
    .then(() => {
        console.log('✅ Database connected successfully');
    })
    .catch((err) => {
        console.error('❌ Database connection failed:', err.message);
        process.exit(1);
    });


module.exports = promisePool;
